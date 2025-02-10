package com.modsen.account.service;

import com.modsen.account.client.DriverServiceClient;
import com.modsen.account.client.KeycloakClient;
import com.modsen.account.client.PassengerServiceClient;
import com.modsen.account.dto.AuthenticateRequest;
import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UpdateUserRequest;
import com.modsen.account.dto.UpdateUserResponse;
import com.modsen.account.dto.UserResponse;
import com.modsen.account.mapper.RequestMapper;
import com.modsen.account.mapper.ResponseMapper;
import com.modsen.account.util.ExceptionMessages;
import com.modsen.account.util.JwtTokenUtil;
import com.modsen.account.util.KeycloakParameters;
import com.modsen.account.util.KeycloakResponseValidator;
import com.modsen.account.util.Roles;
import com.modsen.exception_handler.exception.CreateUserException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return keycloakClient.getToken(Map.of(
                KeycloakParameters.CLIENT_ID, clientId,
                KeycloakParameters.CLIENT_SECRET, clientSecret,
                KeycloakParameters.GRANT_TYPE, OAuth2Constants.PASSWORD,
                KeycloakParameters.USERNAME, request.username(),
                KeycloakParameters.PASSWORD, request.password()
        ));
    }

    @Override
    public UserResponse createUser(RegistrationRequest registrationRequest) throws Exception {
        UserRepresentation user = getUserRepresentation(registrationRequest);
        UsersResource usersResource = getUsersResource();

        Response response = usersResource.create(user);
        KeycloakResponseValidator.validateCreateUserResponse(response);

        String userId = extractUserIdFromResponse(response);
        addRealmRoleToUser(userId, registrationRequest.role().toString());

        try {
            createUserInService(userId, registrationRequest);
        } catch (Exception ex) {
            hardDelete(userId);
            throw new CreateUserException(ExceptionMessages.CREATE_USER_ERROR.format(), ex);
        }

        return responseMapper.toUserResponse(UUID.fromString(userId), registrationRequest);
    }

    @Override
    public void deleteUser(UUID userId) throws Exception {
        jwtTokenUtil.validateAccess(userId);
        UserResource userResource = getUserResource(userId);

        List<String> roles = getUserRoles(userResource);
        try {
            deleteUserFromService(userId, roles);
        } catch (Exception ex) {
            throw new RuntimeException(ExceptionMessages.DELETE_USER_ERROR.format(), ex);
        }

        disableUser(userResource);
    }

    @Override
    public UpdateUserResponse updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        jwtTokenUtil.validateAccess(userId);
        UserResource userResource = getUserResource(userId);

        updateUserRepresentation(userResource, updateUserRequest);

        List<String> roles = getUserRoles(userResource);
        updateUserInService(userId, updateUserRequest, roles);

        return responseMapper.toUpdateUserResponse(userId, updateUserRequest);
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

        user.setAttributes(Map.of("phone_number", List.of(registrationRequest.phoneNumber())));
        user.setCredentials(List.of(createPasswordCredentials(registrationRequest.password())));

        return user;
    }

    private void updateUserRepresentation(UserResource userResource, UpdateUserRequest updateUserRequest) {
        UserRepresentation user = userResource.toRepresentation();
        user.setUsername(updateUserRequest.email());
        user.setEmail(updateUserRequest.email());
        user.setFirstName(updateUserRequest.firstName());
        user.setLastName(updateUserRequest.lastName());

        Optional.ofNullable(updateUserRequest.phoneNumber())
                .ifPresent(phone -> user.getAttributes().put("phone_number", List.of(phone)));

        userResource.update(user);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private void addRealmRoleToUser(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        if (role == null) {
            throw new IllegalArgumentException(ExceptionMessages.ROLE_DOES_NOT_EXIST.format(roleName));
        }

        getUserResource(userId).roles().realmLevel().add(Collections.singletonList(role));
    }

    private void createUserInService(String userId, RegistrationRequest registrationRequest) {
        UUID uuid = UUID.fromString(userId);
        if (registrationRequest.role() == Roles.DRIVER) {
            driverServiceClient.createDriver(requestMapper.registrationRequestToCreateDriverRequest(registrationRequest, uuid));
        } else {
            passengerServiceClient.createPassenger(requestMapper.registrationRequestToCreatePassengerRequest(registrationRequest, uuid));
        }
    }

    private void deleteUserFromService(UUID userId, List<String> roles) {
        if (roles.contains(String.valueOf(Roles.DRIVER))) {
            driverServiceClient.deleteDriver(userId);
        } else if (roles.contains(String.valueOf(Roles.PASSENGER))) {
            passengerServiceClient.deletePassenger(userId);
        }
    }

    private void updateUserInService(UUID userId, UpdateUserRequest updateUserRequest, List<String> roles) {
        if (roles.contains(String.valueOf(Roles.DRIVER))) {
            driverServiceClient.updateDriver(userId, requestMapper.updateUserRequestToUpdateDriverRequest(updateUserRequest));
        } else if (roles.contains(String.valueOf(Roles.PASSENGER))) {
            passengerServiceClient.updatePassenger(userId, requestMapper.updateUserRequestToUpdatePassengerRequest(updateUserRequest));
        }
    }

    private String extractUserIdFromResponse(Response response) {
        String locationHeader = response.getHeaderString("Location");
        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

    private List<String> getUserRoles(UserResource userResource) {
        return userResource.roles().realmLevel().listEffective()
                .stream().map(RoleRepresentation::getName).toList();
    }

    private void hardDelete(String userId) {
        getUsersResource().get(userId).remove();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private UserResource getUserResource(UUID userId) {
        return getUsersResource().get(userId.toString());
    }

    private UserResource getUserResource(String userId) {
        return getUsersResource().get(userId);
    }

}
