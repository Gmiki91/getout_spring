package com.blue.getout.userevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-events")
public class UserEventController {

    @Autowired
    private UserEventService userEventService;

    @PostMapping("/{userId}/join/{eventId}")
    public void joinEvent(@PathVariable String userId, @PathVariable String eventId) {
        userEventService.joinEvent(userId,eventId);
    }
    @DeleteMapping("/{eventId}/participants/{userId}")
    public void leaveEvent(@PathVariable String eventId, @PathVariable String userId){
        userEventService.leaveEvent(eventId,userId);
    }
}