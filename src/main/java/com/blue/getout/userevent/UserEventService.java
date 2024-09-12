package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserEventService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    public ResponseEntity<Event> joinEvent(String userId, String eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        user.getJoinedEvents().add(event);
        event.getParticipants().add(user);

        userRepository.save(user);
        eventRepository.save(event);
        return ResponseEntity.ok(event);
    }
}