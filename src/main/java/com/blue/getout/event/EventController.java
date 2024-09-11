package com.blue.getout.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {
    @Autowired
    EventService eventService;

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, List<Event>>> getFilteredEvents(@PathVariable String userId){
        Map<String, List<Event>> result = eventService.getEventsForUser(userId);
        return ResponseEntity.ok(result);
    }
}
