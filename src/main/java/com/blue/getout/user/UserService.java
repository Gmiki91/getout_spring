package com.blue.getout.user;

import com.blue.getout.utils.Mapper;
import com.blue.getout.utils.NameGenerator;
import com.blue.getout.utils.Utils;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

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

    public ResponseEntity<UserDTO> checkUser(String id) {
        // New user
        if (id == null || id.equals("0")) {
            User newUser = createNewUser();
            return ResponseEntity.ok(mapper.UserEntityToDTO(newUser));
        }

        // Check for smart asses
        if (!isValidUUID(id)) {
            throw new ResourceNotFoundException("Invalid ID");
        }

        // Old user
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(mapper.UserEntityToDTO(user)))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    @Transactional
    public  ResponseEntity<UserDTO> clearNotifications(String id){
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
        user.setId(UUID.randomUUID().toString());
        user.setAvatarUrl(avatar);

        return userRepository.save(user);
    }

    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
