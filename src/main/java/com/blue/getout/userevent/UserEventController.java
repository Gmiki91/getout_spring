package com.blue.getout.userevent;

import com.blue.getout.event.EventDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user-events")
public class UserEventController {
    private final UserEventService userEventService;

    public UserEventController(UserEventService userEventService){
        this.userEventService = userEventService;
    }

    @PostMapping()
    public ResponseEntity<EventDTO> createEventWithUserId(@RequestBody EventDTO event){
        return userEventService.createEventWithUserId(event);
    }
    @PostMapping("/{userId}/join/{eventId}")
    public  ResponseEntity<EventDTO> joinEvent(@PathVariable UUID userId, @PathVariable UUID eventId) {
       return userEventService.joinEvent(eventId,userId);
    }
    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<EventDTO> leaveEvent(@PathVariable UUID eventId, @PathVariable UUID userId){
        return userEventService.leaveEvent(eventId,userId);
    }
    @DeleteMapping("/events/{eventId}")
    public void deleteEvent(@PathVariable UUID eventId){
        userEventService.deleteEvent(eventId);
    }
}