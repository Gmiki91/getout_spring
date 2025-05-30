package com.blue.getout.notification;

import com.blue.getout.event.Event;
import com.blue.getout.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.UUID;

@CrossOrigin
@RepositoryRestResource(path = "notifications")
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    void deleteByEventId(UUID eventId);
    @Modifying
    @Transactional
    void deleteByEventAndUser(Event event, User user);

}
