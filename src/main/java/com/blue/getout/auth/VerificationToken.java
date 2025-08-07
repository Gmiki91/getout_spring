package com.blue.getout.auth;

import com.blue.getout.user.User;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "tokens")
public class VerificationToken {
        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false, unique = true)
        private final String token;

        @OneToOne
        private final User user;

        private final LocalDateTime expiryDate;
    }

