package com.blue.getout.comment;

import com.blue.getout.event.Event;
import com.blue.getout.user.User;
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
    String id;
    ZonedDateTime timestamp;
    String text;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    public Comment(){
        this.id = UUID.randomUUID().toString();
    }
    public Comment(ZonedDateTime timestamp, String text, User user, Event event){
        this.id = UUID.randomUUID().toString();
        this.timestamp=timestamp;
        this.text=text;
        this.user=user;
        this.event=event;
    }
}
