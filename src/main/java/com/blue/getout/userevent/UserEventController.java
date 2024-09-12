package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-events")
public class UserEventController {

    @Autowired
    private UserEventService userEventService;

    @PostMapping("/join/{userId}/{eventId}")
    public ResponseEntity<Event> joinEvent(@PathVariable String userId, @PathVariable String eventId) {
       return userEventService.joinEvent(userId,eventId);
    }
}