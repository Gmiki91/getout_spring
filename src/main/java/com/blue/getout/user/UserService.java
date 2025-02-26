package com.blue.getout.user;

import com.blue.getout.utils.Mapper;
import com.blue.getout.utils.NameGenerator;
import com.blue.getout.utils.Utils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;

import static org.springframework.http.HttpStatus.GONE;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NameGenerator nameGenerator;
    private final Mapper mapper;
    private final Utils utils;

    public UserService(UserRepository userRepository, NameGenerator nameGenerator, Mapper mapper, Utils utils) {
        this.userRepository = userRepository;
        this.nameGenerator = nameGenerator;
        this.mapper = mapper;
        this.utils = utils;
    }

    public ResponseEntity<UserDTO> checkUser(UUID id) {
        // New user
        if (id == null || id.equals(UUID.fromString("c7a2a99c-db48-4c17-891a-126a13b7bb8f"))) {
            User newUser = createNewUser();
            return ResponseEntity.ok(mapper.UserEntityToDTO(newUser));
        }

        // Check for smart asses
//        if (UUID.fromString(id)) {
//            throw new ResourceNotFoundException("Invalid ID");
//        }

        // Old user
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(mapper.UserEntityToDTO(user)))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    public ResponseEntity<UserDTO>  getUserByUsername(final String username) {
        User user =  userRepository.findByName(username).orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated"));
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    @Transactional
    public  ResponseEntity<UserDTO> clearNotifications(UUID id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getNotifications().forEach(notification -> {
            notification.setReadTimestamp(ZonedDateTime.now());
        });
        userRepository.save(user);
        return  ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    private User createNewUser() {
        String randomName = nameGenerator.generateRandomName();
        String avatar = utils.getRandomAvatarUrl();

        User user = new User();
        user.setName(randomName);
        user.setId(UUID.randomUUID());
        user.setAvatarUrl(avatar);
        user.setNotifications(new HashSet<>());

        return userRepository.save(user);
    }
}
