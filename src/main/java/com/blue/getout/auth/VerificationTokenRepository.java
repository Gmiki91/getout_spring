package com.blue.getout.auth;

import com.blue.getout.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@CrossOrigin
@RepositoryRestResource(path = "tokens")
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUser(User user);

    @Query("SELECT t FROM VerificationToken t WHERE t.expiryDate < :now ")
    List<VerificationToken> findExpiredTokens(@Param("now") LocalDateTime now);
}
