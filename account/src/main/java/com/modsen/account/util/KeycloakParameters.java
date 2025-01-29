package com.modsen.account.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum KeycloakParameters {

    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    GRANT_TYPE("grant_type"),
    USERNAME("username"),
    PASSWORD("password");

    private final String parameter;
}
