package com.blue.getout.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {
    @Autowired
    EventRepository eventRepository;

    public Map<String, List<Event>> getEventsForUser(String userId){
       List<Event> joinedEvents =  eventRepository.findEventsJoinedByUser(userId);
       List<Event> otherEvents = eventRepository.findEventsNotJoinedByUser(userId);
        Map<String,List<Event>> result = new HashMap<>();
        result.put("joinedEvents",joinedEvents);
        result.put("otherEvents",otherEvents);
        return result;
    }
}
