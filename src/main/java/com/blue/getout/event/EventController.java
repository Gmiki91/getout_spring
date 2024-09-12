package com.blue.getout.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    EventService eventService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, List<Event>>> getFilteredEvents(@PathVariable String userId){
        Map<String, List<Event>> result = eventService.getEventsForUser(userId);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable String eventId){
        eventService.deleteEvent(eventId);
    }
}
