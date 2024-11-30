package com.blue.getout.notification;

import java.time.ZonedDateTime;

public record NotificationDTO(String eventId,String updateInfo, ZonedDateTime updateStamp, boolean read) {
}
