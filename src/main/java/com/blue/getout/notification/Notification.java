package com.blue.getout.notification;
import com.blue.getout.event.Event;
import com.blue.getout.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private String id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-notifications")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "event_id",nullable = true)
    @JsonBackReference("event-notifications")
    private Event event;

    /**
     * Can either be "event has been updated" or "event has been deleted".
     */
    @Column(name = "update_info")
    private String updateInfo;

    @Column(name = "update_timestamp")
    private ZonedDateTime updateTimestamp;

    /**
     * When new comment is added,
     * if lastCommentTimestamp>readTimestamp => last comment unseen then
     * increment count, update lastCommentTimestamp
     * if lastCommentTimestamp<readTimestamp => last comment seen then
     * reset count to 1, update lastCommentTimestamp
     */
    @Column(name = "comment_count")
    private int commentCount;

    @Column(name = "comment_timestamp")
    private ZonedDateTime commentTimestamp;

    @Column(name = "read_timestamp")
    private ZonedDateTime readTimestamp;

    public Notification(){this.id = UUID.randomUUID().toString();}

    public Notification(User user, Event event, String updateInfo, ZonedDateTime updateTimestamp, int commentCount, ZonedDateTime commentTimestamp, ZonedDateTime readTimestamp) {
        this.id = UUID.randomUUID().toString();;
        this.user = user;
        this.event = event;
        this.updateInfo = updateInfo;
        this.updateTimestamp = updateTimestamp;
        this.commentCount = commentCount;
        this.commentTimestamp = commentTimestamp;
        this.readTimestamp = readTimestamp;
    }
}
