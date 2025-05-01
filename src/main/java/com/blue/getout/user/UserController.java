package com.blue.getout.user;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/check/{id}")
    public ResponseEntity<UserDTO> checkUser(@PathVariable String id) {
        return userService.checkUser(id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserProfile(final Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or user not authenticated. Please sign in again.");
        }
        return userService.getUserByUsername(authentication.getName());
    }

    @PutMapping("/clearNotifications/{id}")
    public ResponseEntity<UserDTO> clearNotifications (@PathVariable UUID id){
        return userService.clearNotifications(id);
    }

    @PutMapping("/changeAvatar/{index}")
    public ResponseEntity<UserDTO> changeAvatar(@PathVariable int index, Authentication authentication){
        return userService.changeAvatar(index, authentication);
    }

    @PutMapping("/changeElo/{value}")
    public ResponseEntity<UserDTO> changeElo(@PathVariable int value, Authentication authentication){
        return userService.changeElo(value, authentication);
    }


}
