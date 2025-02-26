package com.blue.getout.event;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService=eventService;
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, List<EventDTO>>> getFilteredEvents(@PathVariable UUID userId){
        Map<String, List<EventDTO>> result = eventService.getEventsForUser(userId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDTO> patchEvent(@PathVariable UUID eventId, @RequestBody Map<String, Object> updates) {
        return eventService.patchEvent(eventId,updates);

    }

}
