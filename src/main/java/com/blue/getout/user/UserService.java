package com.blue.getout.user;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.UUID;
import com.blue.getout.utils.Mapper;
import com.blue.getout.utils.NameGenerator;
import com.blue.getout.utils.Utils;
import static org.springframework.http.HttpStatus.GONE;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final NameGenerator nameGenerator;
    private final Mapper mapper;
    private final Utils utils;

    public ResponseEntity<UserDTO> checkUser(String id) {
        try {
            //old user
            UUID uuid = UUID.fromString(id);
            return userRepository.findById(uuid)
                    .map(user -> ResponseEntity.ok(mapper.UserEntityToDTO(user)))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        } catch (IllegalArgumentException e) {
            //new user
            User newUser = createGuestUser();
            return ResponseEntity.ok(mapper.UserEntityToDTO(newUser));
        }
    }

    public ResponseEntity<UserDTO> getUserByUsername(final String username) {
        User user = userRepository.findByName(username).orElseThrow(() -> new ResponseStatusException(GONE, "The user account has been deleted or inactivated"));
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    @Transactional
    public ResponseEntity<UserDTO> clearNotifications(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.getNotifications().forEach(notification -> {
            notification.setReadTimestamp(ZonedDateTime.now());
        });
        userRepository.save(user);
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    @Transactional
    public ResponseEntity<UserDTO> changeAvatar(int index, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByName(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String updatedAvatarUrl = utils.getAvatarUrl(index);
        user.setAvatarUrl(updatedAvatarUrl);
        userRepository.save(user);
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    @Transactional
    public ResponseEntity<UserDTO> changeElo(int value, Authentication authentication){
        String username = authentication.getName();
        User user = userRepository.findByName(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setElo(value);
        userRepository.save(user);
        return ResponseEntity.ok(mapper.UserEntityToDTO(user));
    }

    private User createGuestUser() {
        String randomName = nameGenerator.generateRandomName();
        String avatar = utils.getAvatarUrl();

        User user = new User();
        user.setName(randomName);
        user.setId(UUID.randomUUID());
        user.setAvatarUrl(avatar);
        user.setNotifications(new HashSet<>());

        return userRepository.save(user);
    }
}
