package com.blue.getout.auth;

public record RegistrationRequestDTO(
        String username,
        String email,
        String password
) {
}