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
    private final UserEventRepository userEventRepository;
    private final NotificationService notificationService;
    private final Mapper mapper;

    public UserEventService(EventRepository eventRepository, UserRepository userRepository,UserEventRepository userEventRepository, NotificationService notificationService, Mapper mapper) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.userEventRepository = userEventRepository;
        this.notificationService = notificationService;
        this.mapper = mapper;
    }

    @Transactional
    public ResponseEntity<EventDTO> createEventWithUserId(EventDTO eventData) {
        User user = userRepository.findById(eventData.ownerId()).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = mapper.EventDTOToEntity(eventData, user);
        eventRepository.save(event);

        // Create UserEvent link for the owner
        UserEvent userEvent = new UserEvent();
        userEvent.setId(new UserEventId(user.getId(), event.getId()));
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEventRepository.save(userEvent);

        notificationService.createNotification(event,user);
        userRepository.save(user);
        EventDTO eventDTO = mapper.EventEntityToDTO(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDTO);
    }

    @Transactional
    public ResponseEntity<EventDTO> joinEvent(UUID eventId, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        if (event.getMax() != 0 && userEventRepository.countByEvent(event) >= event.getMax()) {
            throw new IllegalArgumentException("The list is full.");
        }
        UserEventId id = new UserEventId(userId, eventId);
        if (userEventRepository.existsById(id)) {
            throw new IllegalArgumentException("Already joined.");
        }

        UserEvent link = new UserEvent();
        link.setId(id);
        link.setUser(user);
        link.setEvent(event);
        event.getParticipants().add(link);

        notificationService.createNotification(event, user);

        Event updated = eventRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        //if user just joined, they couldnt have added a board yet.
        EventDTO eventDTO = mapper.EventEntityToDTO(updated, true,0);
        return ResponseEntity.ok(eventDTO);
    }

    @Transactional
    public ResponseEntity<EventDTO> leaveEvent(UUID eventId, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        userEventRepository.deleteByUserIdAndEventId(userId, eventId);
        notificationService.unsubscribeFromEvent(event, user);

        int boardCount =  userEventRepository.countByEventAndBringingBoardTrue(event);
        Event updated = eventRepository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        EventDTO eventDTO = mapper.EventEntityToDTO(updated, false,boardCount);
        return ResponseEntity.ok(eventDTO);
    }

    @Transactional
    public void toggleBringingBoard(UUID eventId, UUID userId, boolean bringingBoard) {
        UserEvent userEvent = userEventRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation not found"));
        userEvent.setBringingBoard(bringingBoard);
        userEventRepository.save(userEvent);
    }

    @Transactional
    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        notificationService.updateNotification(event, UpdateType.DELETED);
//        this.removeEventFromJoinedLists(event);
        // Save changes to ensure notifications are updated before deleting the event
        // This ensures that the Notification entities are not considered orphans and will not be automatically deleted by Hibernate.
//        eventRepository.save(event);
//        eventRepository.delete(event);

        // delete all UserEvent relations for this event
        userEventRepository.deleteByEvent(event);

        // finally delete the event itself
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

//    @Transactional
//    private void deleteOldEvents(ZonedDateTime timeLimit) {
//        List<Event> oldEvents = eventRepository.findEventsOlderThan(timeLimit);
//        if (!oldEvents.isEmpty()) {
//            oldEvents.forEach(event -> {
//                removeEventFromJoinedLists(event);
//                notificationService.deleteNotifications(event.getId());
//            });
//            eventRepository.deleteAll(oldEvents);
//        }
//    }
    @Transactional
    private void deleteOldEvents(ZonedDateTime timeLimit) {
        List<Event> oldEvents = eventRepository.findEventsOlderThan(timeLimit);
        if (!oldEvents.isEmpty()) {
            oldEvents.forEach(event -> {
                // delete all UserEvent links for this event
                userEventRepository.deleteByEvent(event);

                // delete all notifications related to this event
                notificationService.deleteNotifications(event.getId());
            });

            // finally delete the old events themselves
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

//    private void removeEventFromJoinedLists(Event event) {
//        event.getParticipants().forEach(user -> {
//            user.getJoinedEvents().remove(event);
//            userRepository.save(user);
//        });
//        event.getParticipants().clear();
//    }

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