package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import com.blue.getout.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEventRepository extends JpaRepository<UserEvent, UserEventId> {
    Optional<UserEvent> findByUserIdAndEventId (UUID userId, UUID eventId);
    List<UserEvent> findByEvent(Event event);
    List<UserEvent> findByUserId(UUID userId);
    void deleteByUserIdAndEventId(UUID userId, UUID eventId);
    void deleteByEvent(Event event);
    void deleteByUser(User user);

    // Count all participants  in this event
    int countByEvent(Event event);

    // Count number of boards (participants who are bringing a board)
    int countByEventAndBringingBoardTrue(Event event);

    // Count number of boards for all events, grouped by events
    @Query("SELECT ue.event.id, COUNT(ue) " +
            "FROM UserEvent ue " +
            "WHERE ue.event IN :events AND ue.bringingBoard = true " +
            "GROUP BY ue.event.id")
    List<Object[]> countBoardsForEvents(@Param("events") List<Event> events);
}
