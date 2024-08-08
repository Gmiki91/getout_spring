package com.blue.getout;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NameGenerator nameGenerator;

    @GetMapping("/check/{uuid}")
    public User checkUser(@PathVariable String uuid, HttpServletResponse response) {
        if (uuid != null && !uuid.equals("0")) {
            UUID id = UUID.fromString(uuid);
            System.out.println("uuid nem nulla!");
            User result = userRepository.findById(id).orElse(null);
            if (result != null) {
                System.out.println("User found");
                return result;
            }
        }
        System.out.println("nincs uuid");
        String randomName = nameGenerator.generateRandomName();
        User user = new User();
        user.setName(randomName);
        user.setId(UUID.randomUUID());
        userRepository.save(user);
        return user;
    }
}
