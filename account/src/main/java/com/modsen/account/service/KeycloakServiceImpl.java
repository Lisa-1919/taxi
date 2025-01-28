package com.modsen.account.service;

import com.modsen.account.client.DriverServiceClient;
import com.modsen.account.client.KeycloakClient;
import com.modsen.account.client.PassengerServiceClient;
import com.modsen.account.dto.AuthenticateRequest;
import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UserResponse;
import com.modsen.account.exception.CreateUserException;
import com.modsen.account.mapper.RequestMapper;
import com.modsen.account.mapper.ResponseMapper;
import com.modsen.account.util.ExceptionMessages;
import com.modsen.account.util.JwtTokenUtil;
import com.modsen.account.util.Roles;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;
    private final DriverServiceClient driverServiceClient;
    private final PassengerServiceClient passengerServiceClient;
    private final RequestMapper requestMapper;
    private final ResponseMapper responseMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final KeycloakClient keycloakClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Override
    public Map<String, Object> login(AuthenticateRequest request) {
        Map<String, String> data = new HashMap<>();
        data.put("client_id", clientId);
        data.put("client_secret", clientSecret);
        data.put("grant_type", OAuth2Constants.PASSWORD);
        data.put("username", request.username());
        data.put("password", request.password());

        return keycloakClient.getToken(data);
    }

    @Override
    public UserResponse createUser(RegistrationRequest registrationRequest) throws Exception {
        UserRepresentation user = getUserRepresentation(registrationRequest);
        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);

        if (response.getStatus() != HttpStatus.CREATED.value()) {
            throw new CreateUserException(ExceptionMessages.CREATE_USER_ERROR.format());
        }

        String locationHeader = response.getHeaderString("Location");
        String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        addRealmRoleToUser(userId, registrationRequest.role().toString());

        try {
            if (registrationRequest.role() == Roles.DRIVER) {
                driverServiceClient.createDriver(
                        requestMapper.registrationRequestToCreateDriverRequest(registrationRequest, UUID.fromString(userId))
                );
            } else {
                passengerServiceClient.createPassenger(
                        requestMapper.registrationRequestToCreatePassengerRequest(registrationRequest, UUID.fromString(userId))
                );
            }
        } catch (Exception ex) {
            hardDelete(userId);
            throw new CreateUserException(ExceptionMessages.CREATE_USER_ERROR.format(), ex);
        }

        return responseMapper.toUserResponse(UUID.fromString(userId), registrationRequest);
    }

    @Override
    public void deleteUser(UUID userId) throws Exception {
        jwtTokenUtil.validateAccess(userId);

        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(String.valueOf(userId));

        List<String> realmRoles = userResource
                .roles()
                .realmLevel()
                .listEffective()
                .stream()
                .map(RoleRepresentation::getName)
                .toList();
        try {
            if (realmRoles.contains(String.valueOf(Roles.DRIVER))) {
                driverServiceClient.deleteDriver(userId);
            } else if (realmRoles.contains(String.valueOf(Roles.PASSENGER))) {
                passengerServiceClient.deletePassenger(userId);
            }

        } catch (Exception ex) {
            throw new Exception(ExceptionMessages.DELETE_USER_ERROR.format(), ex);
        }

        disableUser(userResource);
    }

    private void disableUser(UserResource userResource) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(false);
        userResource.update(user);
    }

    private UserRepresentation getUserRepresentation(RegistrationRequest registrationRequest) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(registrationRequest.email());
        user.setFirstName(registrationRequest.firstName());
        user.setLastName(registrationRequest.lastName());
        user.setEmailVerified(false);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phone_number", Collections.singletonList(registrationRequest.phoneNumber()));
        user.setAttributes(attributes);

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(registrationRequest.password());
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        return user;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private void addRealmRoleToUser(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(realm);

        RoleRepresentation role = realmResource
                .roles()
                .get(roleName)
                .toRepresentation();

        if (role == null) {
            throw new IllegalArgumentException(ExceptionMessages.ROLE_DOES_NOT_EXIST.format(roleName));
        }

        UserResource userResource = realmResource.users().get(userId);
        userResource
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));
    }

    private void hardDelete(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).remove();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

}
