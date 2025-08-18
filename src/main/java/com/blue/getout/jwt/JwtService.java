package com.blue.getout.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RequiredArgsConstructor
public class JwtService {
    private final String issuer;
    private final Duration ttl;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(final String email) {
        final JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(email)
                .issuer(issuer)
                .expiresAt(Instant.now().plus(ttl))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

    public String generateRefreshToken(final String email) {
        final JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(email)
                .issuer(issuer)
                .expiresAt(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)).toInstant())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet))
                .getTokenValue();
    }

    public boolean isTokenValid(String token, String email) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String tokenEmail = jwt.getSubject();
            Instant expiresAt = jwt.getExpiresAt();
            if (!tokenEmail.equals(email)) return false;
            assert expiresAt != null;
            return expiresAt.isAfter(Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractEmail(String token){
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token", e);
        }
    }

}