package com.blue.getout.user;

import com.blue.getout.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NameGenerator nameGenerator;

    public ResponseEntity<User> checkUser( String id) {
        if (id != null && !id.equals("0")) {
            System.out.println("uuid nem nulla! " + id);
            User result = userRepository.findById(id).orElse(null);
            if (result != null) {
                System.out.println("User found");
                return ResponseEntity.ok().body(result);
            }
        }
        System.out.println("nincs uuid  "+id);
        String randomName = nameGenerator.generateRandomName();
        User user = new User();
        user.setName(randomName);
        user.setId(String.valueOf(new Random().nextLong()));
        userRepository.save(user);
        return ResponseEntity.ok().body(user);
    }
}
