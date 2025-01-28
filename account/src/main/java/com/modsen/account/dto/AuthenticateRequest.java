package com.modsen.account.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticateRequest(

        @NotNull(message = "Username cannot be null")
        String username,

        @NotNull(message = "Password cannot be null")
        String password
) {
}
