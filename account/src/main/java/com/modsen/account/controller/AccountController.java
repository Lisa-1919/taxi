package com.modsen.account.controller;

import com.modsen.account.dto.AuthenticateRequest;
import com.modsen.account.dto.RegistrationRequest;
import com.modsen.account.dto.UpdateUserRequest;
import com.modsen.account.dto.UpdateUserResponse;
import com.modsen.account.dto.UserResponse;
import com.modsen.account.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Validated @RequestBody AuthenticateRequest request) throws Exception{
        Map<String, Object> response = keycloakService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Validated @RequestBody RegistrationRequest request) throws Exception {
        UserResponse userResponse = keycloakService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) throws Exception {
        keycloakService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable("userId") UUID userId, @Validated @RequestBody UpdateUserRequest updateUserRequest) throws Exception {
        UpdateUserResponse userResponse = keycloakService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

}
