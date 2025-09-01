package com.blue.getout.utils;

import com.blue.getout.comment.Comment;
import com.blue.getout.comment.CommentResponse;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventDTO;
import com.blue.getout.notification.Notification;
import com.blue.getout.notification.NotificationDTO;
import com.blue.getout.user.User;
import com.blue.getout.user.UserDTO;
import com.blue.getout.userevent.UserEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public UserDTO UserEntityToDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getElo(), user.getAvatarUrl(), setNotifications(user.getNotifications()));
    }

    public EventDTO EventEntityToDTO(Event event) {
        return EventEntityToDTO(event, false, 0);
    }

    public EventDTO EventEntityToDTO(Event event, boolean joined, long boardCount) {
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getLatLng(),
                event.getTime(),
                event.getEndTime(),
                event.getParticipants().stream()
                        .map(UserEvent::getUser)
                        .map(this::UserEntityToDTO)
                        .collect(Collectors.toSet()),
                event.getMin(),
                event.getMax(),
                event.getInfo(),
                event.getRecurring(),
                event.getOwner().getId(),
                joined,
                boardCount
        );
    }

    public Event EventDTOToEntity(EventDTO eventData, User owner) {
        return new Event(eventData.title(), eventData.location(), eventData.latLng(), eventData.time(), eventData.endTime(),
                eventData.min(), eventData.max(), eventData.info(), eventData.recurring(), owner);
    }

    public CommentResponse CommentEntityToResponse(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getText(), comment.getTimestamp(), comment.getUser().getName(), comment.getUser().getAvatarUrl());
    }

    // PRIVATE METHODS
    private Set<NotificationDTO> setNotifications(Set<Notification> notifications) {
        List<NotificationDTO> result = new ArrayList<>();
        notifications.forEach(notification -> {
            // for status notifications (which is just delete notifications for now, but will add field modifications later)
            if (notification.getUpdateInfo() != null && !notification.getUpdateInfo().isEmpty()) {
                boolean read = notification.getReadTimestamp().isAfter(notification.getUpdateTimestamp());
                UUID eventID = notification.getEvent() == null ? null : notification.getEvent().getId(); // in case of Delete notification
                NotificationDTO n1 = new NotificationDTO(eventID,
                        notification.getUpdateInfo(), notification.getUpdateTimestamp(), read);
                result.add(n1);
            }
            // For comment notifications
            // if there are unseen comments but the event has been deleted, dont show comments
            if (notification.getCommentCount() > 0 && notification.getEvent() != null) {
                boolean read = notification.getReadTimestamp().isAfter(notification.getCommentTimestamp());
                String text = notification.getCommentCount() > 1 ? " new comments in '" : " new comment in '";
                NotificationDTO n2 = new NotificationDTO(notification.getEvent().getId(),
                        notification.getCommentCount() + text + notification.getEvent().getTitle() + "'",
                        notification.getCommentTimestamp(), read);
                result.add(n2);
            }
        });
        return result.stream().sorted(Comparator.comparing(NotificationDTO::updateStamp).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
