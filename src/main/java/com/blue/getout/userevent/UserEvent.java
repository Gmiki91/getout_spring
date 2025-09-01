package com.blue.getout.userevent;

import com.blue.getout.event.Event;
import com.blue.getout.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_event")
public class UserEvent {

    @EmbeddedId
    private UserEventId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("eventId")
    private Event event;

    private boolean bringingBoard  = false;
}
