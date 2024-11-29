package com.blue.getout.utils;

import com.blue.getout.comment.Comment;
import com.blue.getout.comment.CommentResponse;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventDTO;
import com.blue.getout.user.User;
import com.blue.getout.user.UserDTO;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public UserDTO UserEntityToDTO(User user){
        return new UserDTO(user.getId(),user.getName(), user.getAvatarUrl(),user.getNotifications());
    }

    public EventDTO EventEntityToDTO(Event event){
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getLatLng(),
                event.getTime(),
                event.getEndTime(),
                mapParticipants(event.getParticipants()),
                event.getMin(),
                event.getMax(),
                event.getInfo(),
                event.getRecurring(),
                event.getOwner().getId());
    }

    public Event EventDTOToEntity(EventDTO eventData, User user){
        return new Event(eventData.title(), eventData.location(), eventData.latLng(), eventData.time(),eventData.endTime(),
                eventData.min(), eventData.max(), Set.of(user), eventData.info(),eventData.recurring(), user);
    }
    public CommentResponse CommentEntityToResponse(Comment comment){
        return new CommentResponse(comment.getId(),comment.getText(),comment.getTimestamp(),comment.getUser().getName(),comment.getUser().getAvatarUrl());
    }

    // PRIVATE METHODS
    private Set<UserDTO> mapParticipants(Set<User> participants) {
        return participants.stream()
                .map(user -> new UserDTO(user.getId(), user.getName(),user.getAvatarUrl(),user.getNotifications()))
                .collect(Collectors.toSet());
    }
}
