package com.blue.getout.userevent;

import com.blue.getout.event.EventDTO;
import com.blue.getout.event.EventData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-events")
public class UserEventController {
    private final UserEventService userEventService;

    public UserEventController(UserEventService userEventService){
        this.userEventService = userEventService;
    }

    @PostMapping()
    public ResponseEntity<EventDTO> createEventWithUserId(@RequestBody EventData event){
        return userEventService.createEventWithUserId(event);
    }
    @PostMapping("/{userId}/join/{eventId}")
    public  ResponseEntity<EventDTO> joinEvent(@PathVariable String userId, @PathVariable String eventId) {
       return userEventService.modifyEventParticipation(userId,eventId,true);
    }
    @DeleteMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<EventDTO> leaveEvent(@PathVariable String eventId, @PathVariable String userId){
        return userEventService.modifyEventParticipation(eventId,userId,false);
    }
}