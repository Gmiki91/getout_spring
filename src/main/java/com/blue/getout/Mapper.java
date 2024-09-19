package com.blue.getout;

import com.blue.getout.event.Event;
import com.blue.getout.event.EventDTO;
import com.blue.getout.user.User;
import com.blue.getout.user.UserDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class Mapper {

    public UserDTO toDTO(User user){
        return new UserDTO(user.getId(),user.getName());
    }

    public EventDTO toDTO(Event event){
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getLatLng(),
                event.getTime(),
                event.getParticipants().stream()
                        .map(user -> new UserDTO(user.getId(), user.getName())).collect(Collectors.toSet()),
                event.getMin(),
                event.getMax(),
                event.getInfo(),
                event.getOwner().getId());
    }
}
