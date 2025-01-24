package com.modsen.account.util;

public enum Roles {
    DRIVER ("DRIVER"),
    PASSENGER("PASSENGER");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
