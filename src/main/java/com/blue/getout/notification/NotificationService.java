package com.blue.getout.notification;

import com.blue.getout.event.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public void deleteNotifications(String eventId){
        this.notificationRepository.deleteByEventId(eventId);
    }

    @Transactional
    public void updateDeleteNotification(Event event) {
        Set<Notification> notifications = event.getNotifications().isEmpty() ? createNotifications(event) : event.getNotifications();
        notifications.forEach(notification -> {
            if (!event.getOwner().getId().equals(notification.getUser().getId())) { //only the owner can  delete the event, they dont need notification
                notification.setUpdateTimestamp(ZonedDateTime.now());
                notification.setUpdateInfo("The event '" + event.getTitle() + "' has been deleted.");
            }
            notification.setEvent(null);
        });
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void updateCommentNotification(Event event, String userId) {
        Set<Notification> notifications = event.getNotifications().isEmpty() ? createNotifications(event) : event.getNotifications();
        notifications.forEach(notification -> {
            if (!notification.getUser().getId().equals(userId)) { // the comment's author need no notification
                if (notification.getCommentTimestamp().isAfter(notification.getReadTimestamp())) { //last update has not been seen yet
                    notification.setCommentCount(notification.getCommentCount() + 1);
                } else {
                    notification.setCommentCount(1);
                }
                notification.setCommentTimestamp(ZonedDateTime.now());
            }
        });
        notificationRepository.saveAll(notifications);
    }

    private Set<Notification> createNotifications(Event event) {
        Set<Notification> notifications = new HashSet<>();
        event.getParticipants().forEach(user -> {
            Notification notification = new Notification();
            notification.setEvent(event);
            notification.setUser(user);
            notification.setUpdateTimestamp(ZonedDateTime.now());
            notification.setCommentTimestamp(ZonedDateTime.now());
            notification.setReadTimestamp(ZonedDateTime.now().minusHours(1));
            notifications.add(notification);
        });
        return notifications;
    }
}
