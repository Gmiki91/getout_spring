package com.blue.getout.user.registration;

import com.blue.getout.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationMapper {
    public User toEntity(RegistrationRequestDto registrationRequestDto) {
        final var user = new User();

        user.setEmail(registrationRequestDto.email());
        user.setName(registrationRequestDto.username());
        user.setPassword(registrationRequestDto.password());

        return user;
    }

    public RegistrationResponseDto toRegistrationResponseDto(
            final User user) {

        return new RegistrationResponseDto(
                user.getEmail(), user.getName());
    }
}
