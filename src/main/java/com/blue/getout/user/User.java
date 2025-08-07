package com.blue.getout.user;
import com.blue.getout.comment.Comment;
import com.blue.getout.event.Event;
import com.blue.getout.notification.Notification;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "\"users\"")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-comments")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-notifications")
    private Set<Notification> notifications;

    @ManyToMany
    @JoinTable(
            name = "user_event",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @JsonManagedReference("user-participants")
    private Set<Event> joinedEvents;

    @Column(length = 1000)
    private String refreshToken;

    private String avatarUrl;
    private String email;
    private String password;
    private boolean emailVerified = false;
    private int elo;
}