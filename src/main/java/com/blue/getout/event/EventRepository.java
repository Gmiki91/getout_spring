package com.blue.getout.event;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.time.ZonedDateTime;
import java.util.List;

@CrossOrigin
@RepositoryRestResource(path = "events")
public interface EventRepository extends JpaRepository<Event,String> {

    // Find events the user has joined
    @Query("SELECT e FROM Event e JOIN e.participants u WHERE u.id = :userId")
    List<Event> findEventsJoinedByUser(@Param("userId") String userId, Sort sort);

    // Find events the user has not joined
    @Query("SELECT e FROM Event e WHERE e.id NOT IN (SELECT e2.id FROM Event e2 JOIN e2.participants u WHERE u.id = :userId)")
    List<Event> findEventsNotJoinedByUser(@Param("userId") String userId,Sort sort);

    // Find events older than 24 hours
    @Query("SELECT e FROM Event e WHERE e.time < :timeLimit")
    List<Event> findEventsOlderThan(@Param("timeLimit") ZonedDateTime timeLimit);
}
