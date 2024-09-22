package com.blue.getout.userevent;

import com.blue.getout.Mapper;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventDTO;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    public UserEventService(EventRepository eventRepository, UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    public ResponseEntity<EventDTO> createEventWithUserId(EventDTO eventData) {
        User user = userRepository.findById(eventData.ownerId()).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = new Event(eventData.title(), eventData.location(), eventData.latLng(), eventData.time(), eventData.min(), eventData.max(), Set.of(user), eventData.info(), user);
        eventRepository.save(event);
        user.getJoinedEvents().add(event);
        userRepository.save(user);
        EventDTO eventDTO = mapper.toDTO(event);
        return ResponseEntity.ok(eventDTO);
    }

    @Transactional
    public ResponseEntity<EventDTO> modifyEventParticipation(String eventId, String userId, boolean isJoining) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));

        boolean userModified, eventModified;
        if (isJoining) {
            userModified = user.getJoinedEvents().add(event);
            eventModified = event.getParticipants().add(user);
        } else {
            userModified = user.getJoinedEvents().remove(event);
            eventModified = event.getParticipants().remove(user);
        }

        if (userModified && eventModified) {
            userRepository.save(user);
            EventDTO eventDTO = mapper.toDTO(event);
            return ResponseEntity.ok(eventDTO);
        } else {
            throw new IllegalArgumentException("The list did not contain the entity.");
        }
    }


    @Transactional
    public void deleteEvent(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        this.removeEventFromJoinedLists(event);
        eventRepository.save(event);
        eventRepository.delete(event);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void deleteOldEvents() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timeLimit = now.minusHours(24);
        List<Event> oldEvents = eventRepository.findEventsOlderThan(timeLimit);
        if (!oldEvents.isEmpty()) {
            oldEvents.forEach(this::removeEventFromJoinedLists);
            eventRepository.deleteAll(oldEvents);
        }
    }

    private void removeEventFromJoinedLists(Event event) {
        event.getParticipants().forEach(user -> {
            user.getJoinedEvents().remove(event);
            userRepository.save(user);
        });
        event.getParticipants().clear();

    }
}