package com.blue.getout.user;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<UserDTO> checkUser(@PathVariable String id) {
        return userService.checkUser(id);
    }

    @PutMapping("/clearNotifications/{id}")
    public ResponseEntity<UserDTO> clearNotifications (@PathVariable String id){
        return userService.clearNotifications(id);
    }
}
