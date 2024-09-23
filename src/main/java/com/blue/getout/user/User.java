package com.blue.getout.user;
import com.blue.getout.comment.Comment;
import com.blue.getout.event.Event;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter @Setter
@Entity
public class User {

    @Id
    private String id;

    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @JsonManagedReference
    private Set<Event> joinedEvents;
}