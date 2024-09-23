package com.blue.getout.user;

import com.blue.getout.Mapper;
import com.blue.getout.NameGenerator;
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
            User result = userRepository.findById(id).orElse(null);
            if (result != null) {

                return ResponseEntity.ok(mapper.UserEntityToDTO(result));
            }
        }
        String randomName = nameGenerator.generateRandomName();
        User user = new User();
        user.setName(randomName);
        user.setId(UUID.randomUUID().toString());
        userRepository.save(user);
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }
}
