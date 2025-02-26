package com.blue.getout.userevent;

import com.blue.getout.event.UpdateType;
import com.blue.getout.notification.NotificationService;
import com.blue.getout.utils.Mapper;
import com.blue.getout.event.Event;
import com.blue.getout.event.EventDTO;
import com.blue.getout.event.EventRepository;
import com.blue.getout.user.User;
import com.blue.getout.user.UserRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final Mapper mapper;

    public UserEventService(EventRepository eventRepository, UserRepository userRepository, NotificationService notificationService, Mapper mapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Transactional
    public ResponseEntity<EventDTO> createEventWithUserId(EventDTO eventData) {
        User user = userRepository.findById(eventData.ownerId()).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = mapper.EventDTOToEntity(eventData, user);
        eventRepository.save(event);
        notificationService.subscribeToEvent(event,user);
        userRepository.save(user);
        EventDTO eventDTO = mapper.EventEntityToDTO(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDTO);
    }

    @Transactional
    public ResponseEntity<EventDTO> modifyEventParticipation(UUID eventId, UUID userId, boolean isJoining) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        if (isJoining && event.getMax() != 0 && event.getParticipants().size() >= event.getMax()) {
            throw new IllegalArgumentException("The list is full.");
        }
        if (!isJoining && event.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("The list is already empty.");
        }
        boolean userModified, eventModified;
        if (isJoining) {
            userModified = user.getJoinedEvents().add(event);
            eventModified = event.getParticipants().add(user);
            notificationService.subscribeToEvent(event,user);
        } else {
            userModified = user.getJoinedEvents().remove(event);
            eventModified = event.getParticipants().remove(user);
            notificationService.unsubscribeFromEvent(event,user);
        }

        if (userModified && eventModified) {
            userRepository.save(user);
            EventDTO eventDTO = mapper.EventEntityToDTO(event);
            return ResponseEntity.ok(eventDTO);
        } else {
            throw new IllegalArgumentException("The list did not contain the entity.");
        }
    }

    @Transactional
    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        notificationService.updateNotification(event, UpdateType.DELETED);
        this.removeEventFromJoinedLists(event);
        // Save changes to ensure notifications are updated before deleting the event
        // This ensures that the Notification entities are not considered orphans and will not be automatically deleted by Hibernate.
        eventRepository.save(event);
        eventRepository.delete(event);
    }

    @Transactional
    @Scheduled(cron = "@hourly")
    public void processOldEvents() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime timeLimit = now.minusHours(1);
        deleteOldEvents(timeLimit);
        processRecurringEvents(timeLimit);
    }

    @Transactional
    private void deleteOldEvents(ZonedDateTime timeLimit) {
        List<Event> oldEvents = eventRepository.findEventsOlderThan(timeLimit);
        if (!oldEvents.isEmpty()) {
            oldEvents.forEach(event -> {
                removeEventFromJoinedLists(event);
                notificationService.deleteNotifications(event.getId());
            });
            eventRepository.deleteAll(oldEvents);
        }
    }

    @Transactional
    private void processRecurringEvents(ZonedDateTime timeLimit) {
        List<Event> recurringEvents = eventRepository.findRecurringEventsOlderThan(timeLimit);
        if (!recurringEvents.isEmpty()) {
            recurringEvents.forEach(this::updateRecurringEvent);
            eventRepository.saveAll(recurringEvents);
        }
    }

    private void removeEventFromJoinedLists(Event event) {
        event.getParticipants().forEach(user -> {
            user.getJoinedEvents().remove(event);
            userRepository.save(user);
        });
        event.getParticipants().clear();
    }

    private void updateRecurringEvent(Event event) {
        switch (event.getRecurring()) {
            case "daily":
                event.setTime(event.getTime().plusDays(1));
                event.setEndTime(event.getEndTime().plusDays(1));
                break;
            case "weekly":
                event.setTime(event.getTime().plusWeeks(1));
                event.setEndTime(event.getEndTime().plusWeeks(1));
                break;
            case "monthly":
                event.setTime(event.getTime().plusMonths(1));
                event.setEndTime(event.getEndTime().plusMonths(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown recurrence type: " + event.getRecurring());
        }
    }
}