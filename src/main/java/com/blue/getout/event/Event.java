package com.blue.getout.event;

import com.blue.getout.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LatLng getLatLng() {return latLng;}

    public void setLatLng(LatLng latLng) {this.latLng = latLng;}

    public ZonedDateTime getTime() {return time;}

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
