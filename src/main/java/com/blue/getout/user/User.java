package com.blue.getout.user;
import com.blue.getout.userevent.UserEvent;
import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
public class User {

    @Id
    private String id;

    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserEvent> joinedEvents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserEvent> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(Set<UserEvent> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }
}