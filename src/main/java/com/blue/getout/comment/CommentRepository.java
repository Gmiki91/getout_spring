package com.blue.getout.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
@CrossOrigin
@RepositoryRestResource(path = "comments")
public interface CommentRepository extends JpaRepository<Comment,String> {

    // Method to fetch all comments by eventId
    List<Comment> findByEventId(String eventId);
}
