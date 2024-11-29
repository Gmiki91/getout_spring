package com.blue.getout.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path = "notifications")
public interface NotificationRepository extends JpaRepository<Notification,String> {
}
