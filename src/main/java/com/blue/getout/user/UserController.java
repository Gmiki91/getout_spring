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
