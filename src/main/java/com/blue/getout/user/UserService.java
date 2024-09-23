package com.blue.getout.user;

import com.blue.getout.NameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NameGenerator nameGenerator;
    private final  Mapper mapper;

    public UserService(UserRepository userRepository, NameGenerator nameGenerator, Mapper mapper){
        this.userRepository=userRepository;
        this.nameGenerator=nameGenerator;
        this.mapper=mapper;
    }
    public ResponseEntity<UserDTO> checkUser( String id) {
        if (id != null && !id.equals("0")) {
            System.out.println("uuid nem nulla! " + id);
            User result = userRepository.findById(id).orElse(null);
            if (result != null) {
                System.out.println("User found");
                return ResponseEntity.ok(result);
            }
        }
        System.out.println("nincs uuid  "+id);
        String randomName = nameGenerator.generateRandomName();
        User user = new User();
        user.setName(randomName);
        user.setId(UUID.randomUUID().toString());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
