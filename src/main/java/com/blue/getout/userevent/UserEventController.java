package com.blue.getout.userevent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-events")
public class UserEventController {

    @Autowired
    private UserEventService userEventService;

    @PostMapping("/join/{userId}/{eventId}")
    public void joinEvent(@PathVariable String userId, @PathVariable String eventId) {
        userEventService.joinEvent(userId,eventId);
    }
}