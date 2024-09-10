package com.blue.getout.event;

import com.blue.getout.userevent.UserEvent;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.Set;

@Entity
@Table(name="events")
public class Event {
    @Id
    @Column(name="id")
    private String id;

    @Column(name="title")
    private String title;

    @Column(name="location")
    private String location;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="time")
    private ZonedDateTime time;

    @Column(name="min_people")
    private int min;

    @Column(name="max_people")
    private int max;

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL)
    private Set<UserEvent> participiants;

    public Event(){
        this.id = String.valueOf(new Random().nextLong());
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

    public ZonedDateTime getTime() {
        return time;
    }

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

    public void setParticipiants(Set<UserEvent>participiants){
        this.participiants=participiants;
    }
    public Set<UserEvent> getParticipiants(){
        return participiants;
    }
}
