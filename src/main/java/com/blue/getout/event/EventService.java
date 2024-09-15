package com.blue.getout.event;

import com.blue.getout.Mapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final Mapper mapper;
    public EventService(EventRepository eventRepository, Mapper mapper){
        this.eventRepository=eventRepository;
        this.mapper=mapper;
    }
    public Map<String, List<EventDTO>> getEventsForUser(String userId) {
        List<EventDTO> joinedEvents = eventRepository.findEventsJoinedByUser(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
        List<EventDTO> otherEvents = eventRepository.findEventsNotJoinedByUser(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();

        Map<String, List<EventDTO>> result = new HashMap<>();
        result.put("joinedEvents", joinedEvents);
        result.put("otherEvents", otherEvents);
        return result;
    }

    public void deleteEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        event.getParticipants().forEach(user -> user.getJoinedEvents().remove(event));
        event.getParticipants().clear();
        eventRepository.save(event);
        eventRepository.delete(event);
    }
}
