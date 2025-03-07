package com.blue.getout.user;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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
        return userService.getUserByUsername(authentication.getName());
    }

    @PutMapping("/clearNotifications/{id}")
    public ResponseEntity<UserDTO> clearNotifications (@PathVariable UUID id){
        return userService.clearNotifications(id);
    }
}
