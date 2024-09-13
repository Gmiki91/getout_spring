package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import com.blue.getout.event.EventData;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;

@Service
public class UserEventService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public ResponseEntity<Event> createEventWithUserId(EventData eventData){
        User user = userRepository.findById(eventData.ownerId()).orElseThrow(() -> new RuntimeException("User not found"));
        Event event =new Event(eventData.title(),eventData.location(), ZonedDateTime.parse(eventData.time()),eventData.min(),eventData.max(), Set.of(user),eventData.info());
        eventRepository.save(event);
        user.getJoinedEvents().add(event);
        userRepository.save(user);
        return ResponseEntity.ok(event);
    }

    public void joinEvent(String userId, String eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        user.getJoinedEvents().add(event);
        event.getParticipants().add(user);

        userRepository.save(user);
        eventRepository.save(event);
    }

    public void leaveEvent(String eventId, String userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        boolean userRemoved = event.getParticipants().remove(user);
        boolean eventRemoved = user.getJoinedEvents().remove(event);
        if (userRemoved && eventRemoved) {
            eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("User not found in the participants list");
        }
    }
}