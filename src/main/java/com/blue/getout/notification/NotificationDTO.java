package com.blue.getout.notification;

import java.time.ZonedDateTime;
import java.util.UUID;

public record NotificationDTO(UUID eventId, String updateInfo, ZonedDateTime updateStamp, boolean read) {
}
