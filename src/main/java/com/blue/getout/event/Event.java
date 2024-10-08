package com.blue.getout.event;

import com.blue.getout.comment.Comment;
import com.blue.getout.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
@Getter @Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "lat_lng")
    @Embedded
    private LatLng latLng;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time")
    private ZonedDateTime time;

    @Column(name = "min_people")
    private int min;

    @Column(name = "max_people")
    private int max;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments;  // Comments for the event

    @ManyToMany(mappedBy = "joinedEvents")
    @JsonBackReference
    private Set<User> participants;

    @Column(name = "info")
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private User owner;

    public Event() {
        this.id = UUID.randomUUID().toString();
    }

    public Event(String title, String location,LatLng latLng, ZonedDateTime time, int min, int max, Set<User> participants, String info,User owner) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.location = location;
        this.latLng = latLng;
        this.time = time;
        this.min = min;
        this.max = max;
        this.participants = participants;
        this.info = info;
        this.owner = owner;
    }
}
