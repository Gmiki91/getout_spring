package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import com.blue.getout.event.EventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-events")
public class UserEventController {

    @Autowired
    private UserEventService userEventService;

    @PostMapping()
    public ResponseEntity<Event> createEventWithUserId(@RequestBody EventData event){
        return userEventService.createEventWithUserId(event);
    }
    @PostMapping("/{userId}/join/{eventId}")
    public void joinEvent(@PathVariable String userId, @PathVariable String eventId) {
        userEventService.joinEvent(userId,eventId);
    }
    @DeleteMapping("/{eventId}/participants/{userId}")
    public void leaveEvent(@PathVariable String eventId, @PathVariable String userId){
        userEventService.leaveEvent(eventId,userId);
    }
}