package com.blue.getout.user;

import com.blue.getout.notification.NotificationDTO;

import java.util.Set;
import java.util.UUID;

public record UserDTO (UUID id, String name,String email, String avatarUrl, Set<NotificationDTO> notifications){}
