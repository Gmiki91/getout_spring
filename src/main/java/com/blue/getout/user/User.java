package com.blue.getout.user;
import com.blue.getout.event.Event;
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class User {

    @Id
    private String id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> joinedEvents;

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

    public Set<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void setJoinedEvents(Set<Event> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }
}