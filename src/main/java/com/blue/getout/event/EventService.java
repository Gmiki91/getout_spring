package com.blue.getout.event;

import com.blue.getout.utils.Mapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final Mapper mapper;
    public EventService(EventRepository eventRepository, Mapper mapper){
        this.eventRepository=eventRepository;
        this.mapper=mapper;
    }
    public Map<String, List<EventDTO>> getEventsForUser(String userId) {

        List<EventDTO> joinedEvents = getEvents(
                id -> eventRepository.findEventsJoinedByUser(id, Sort.by(Sort.Direction.ASC, "time")), userId);

        List<EventDTO> otherEvents = getEvents(
                id -> eventRepository.findEventsNotJoinedByUser(id, Sort.by(Sort.Direction.ASC, "time")), userId);

        Map<String, List<EventDTO>> result = new HashMap<>();
        result.put("joinedEvents", joinedEvents);
        result.put("otherEvents", otherEvents);
        return result;
    }
    private List<EventDTO> getEvents(Function<String, List<Event>> eventFinder, String userId) {
        return eventFinder.apply(userId)
                .stream()
                .map(mapper::EventEntityToDTO)
                .toList();
    }
}
