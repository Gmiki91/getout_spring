package com.blue.getout.event;

import com.blue.getout.notification.NotificationService;
import com.blue.getout.utils.Mapper;
import com.blue.getout.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final Mapper mapper;
    private final Utils utils;

    public EventService(EventRepository eventRepository, NotificationService notificationService, Mapper mapper,Utils utils) {
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
        this.utils=utils;
    }

    public Map<String, List<EventDTO>> getEventsForUser(UUID userId) {

        List<EventDTO> joinedEvents = getEvents(
                id -> eventRepository.findEventsJoinedByUser(id, Sort.by(Sort.Direction.ASC, "time")), userId);

        List<EventDTO> otherEvents = getEvents(
                id -> eventRepository.findEventsNotJoinedByUser(id, Sort.by(Sort.Direction.ASC, "time")), userId);

        Map<String, List<EventDTO>> result = new HashMap<>();
        result.put("joinedEvents", joinedEvents);
        result.put("otherEvents", otherEvents);
        return result;
    }

    public ResponseEntity<EventDTO> patchEvent(UUID eventId, Map<String, Object> updates) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + eventId));
        String originalTitle = event.getTitle(); // in case of title change, notification should show the old one
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(Event.class, key); // Get field by name
            if (field != null) {
                field.setAccessible(true);
                Object convertedValue = utils.convertStringToZonedDateTime(field.getType(), value);
                // Update the field with the converted value
                ReflectionUtils.setField(field, event, convertedValue);
            }
        });
        Event copy = new Event();
        ReflectionUtils.shallowCopyFieldState(event, copy);
        copy.setTitle(originalTitle);
        Event updatedEvent = eventRepository.save(event);
        notificationService.updateNotification(copy, UpdateType.MODIFIED);

        EventDTO response = mapper.EventEntityToDTO(updatedEvent);
        return ResponseEntity.ok(response);
    }

    private List<EventDTO> getEvents(Function<UUID, List<Event>> eventFinder, UUID userId) {
        return eventFinder.apply(userId)
                .stream()
                .map(mapper::EventEntityToDTO)
                .toList();
    }
}
