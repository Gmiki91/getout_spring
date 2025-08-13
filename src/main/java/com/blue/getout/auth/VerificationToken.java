package com.blue.getout.auth;

import com.blue.getout.user.User;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class VerificationToken {
        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false, unique = true)
        private  String token;

        @OneToOne
        private  User user;

        private  LocalDateTime expiryDate;

        public VerificationToken(String token, User user, LocalDateTime expiryDate) {
                this.token = token;
                this.user = user;
                this.expiryDate = expiryDate;
        }
    }

