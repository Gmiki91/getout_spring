package com.blue.getout.user.registration;

public record RegistrationRequestDto(
        String username,
        String email,
        String password
) {
}