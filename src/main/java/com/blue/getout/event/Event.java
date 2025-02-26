package com.blue.getout.event;

import com.blue.getout.comment.Comment;
import com.blue.getout.notification.Notification;
import com.blue.getout.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    private UUID id;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private ZonedDateTime endTime;

    @Column(name = "min_people")
    private int min;

    @Column(name = "max_people")
    private int max;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Comment> comments;

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private Set<Notification> notifications = new HashSet<>();

    @ManyToMany(mappedBy = "joinedEvents")
    @JsonBackReference("user-participants")
    private Set<User> participants;

    @Column(name = "info")
    private String info;

    @Column(name="recurring")
    private String recurring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference("event-owner")
    private User owner;

    public Event() {
        this.id = UUID.randomUUID();
    }

    public Event(String title, String location,LatLng latLng, ZonedDateTime time, ZonedDateTime endTime,int min, int max, Set<User> participants, String info,String recurring,User owner) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.location = location;
        this.latLng = latLng;
        this.time = time;
        this.endTime = endTime;
        this.min = min;
        this.max = max;
        this.participants = participants;
        this.info = info;
        this.recurring = recurring;
        this.owner = owner;
    }
}
