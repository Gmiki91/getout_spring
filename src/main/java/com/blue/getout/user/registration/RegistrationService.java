package com.blue.getout.user.registration;

import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import com.blue.getout.utils.Utils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Utils utils;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByName(user.getName()) || userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Username or Email already exists");}
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAvatarUrl(utils.getRandomAvatarUrl());
        user.setNotifications(new HashSet<>());
        return userRepository.save(user);
    }
}
