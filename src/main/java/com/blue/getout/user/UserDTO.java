package com.blue.getout.user;

import com.blue.getout.notification.NotificationDTO;

import java.util.Set;

public record UserDTO (String id, String name, String avatarUrl, Set<NotificationDTO> notifications){}
