package com.blue.getout.notification;

import com.blue.getout.event.Event;
import com.blue.getout.event.UpdateType;
import com.blue.getout.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public void deleteNotifications(UUID eventId){
        this.notificationRepository.deleteByEventId(eventId);
    }

    @Transactional
    public void updateNotification(Event event, UpdateType type) {
        Set<Notification> notifications = event.getNotifications().isEmpty() ? createNotifications(event) : event.getNotifications();
        notifications.forEach(notification -> {
        boolean isOwner = event.getOwner().getId().equals(notification.getUser().getId());
            if(type.equals(UpdateType.DELETED)) {
                if (isOwner) {
                    notificationRepository.delete(notification);  // the notification entity of the owner can be deleted
                }else{
                    notification.setEvent(null); // for subscribed users, we send a deleted notification
                }
            }
            if (!isOwner) { //only the owner can update the event, they dont need notification
                notification.setUpdateTimestamp(ZonedDateTime.now());
                notification.setUpdateInfo("The event '" + event.getTitle() + "' has been "+type.label+".");
            }
        });
    }

    @Transactional
    public void updateCommentNotification(Event event, UUID userId) {
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
    }

    @Transactional
    public void subscribeToEvent(Event event, User user){
        Notification notification = createNotification(event,user);
        user.getJoinedEvents().add(event);
    }

    @Transactional
    public void unsubscribeFromEvent(Event event, User user) {
        event.getNotifications().removeIf(notification -> notification.getUser().equals(user));
        notificationRepository.deleteByEventAndUser(event, user);
    }

    private Set<Notification> createNotifications(Event event) {
        Set<Notification> notifications = new HashSet<>();
        event.getParticipants().forEach(user -> {
            Notification notification = this.createNotification(event,user);
            notifications.add(notification);
        });
        return notifications;
    }

    public Notification createNotification(Event event, User user){
        Notification notification = new Notification();
        notification.setEvent(event);
        notification.setUser(user);
        notification.setUpdateTimestamp(ZonedDateTime.now());
        notification.setCommentTimestamp(ZonedDateTime.now());
        notification.setReadTimestamp(ZonedDateTime.now().minusHours(1));
        notificationRepository.save(notification);
        return notification;
    }
}
