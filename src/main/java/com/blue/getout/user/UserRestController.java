package com.blue.getout.user;
import com.blue.getout.NameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NameGenerator nameGenerator;

    @GetMapping("/check/{id}")
    public User checkUser(@PathVariable String id, HttpServletResponse response) {
        if (id != null && !id.equals("0")) {
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
        user.setId(String.valueOf(new Random().nextLong()));
        userRepository.save(user);
        return user;
    }
}
