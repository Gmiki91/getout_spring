package com.blue.getout.comment;

import com.blue.getout.event.Event;
import com.blue.getout.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
public class Comment {
    @Id
    UUID id;
    ZonedDateTime timestamp;
    String text;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-comments")
    User user;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference("event-comments")
    Event event;

    public Comment(){
        this.id = UUID.randomUUID();
    }
    public Comment(ZonedDateTime timestamp, String text, User user, Event event){
        this.id = UUID.randomUUID();
        this.timestamp=timestamp;
        this.text=text;
        this.user=user;
        this.event=event;
    }
}
