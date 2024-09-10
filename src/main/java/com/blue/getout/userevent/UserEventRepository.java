package com.blue.getout.userevent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RepositoryRestResource(path="user_event")
public interface UserEventRepository extends JpaRepository<UserEvent,String> {
}
