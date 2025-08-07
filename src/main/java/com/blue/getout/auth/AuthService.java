package com.blue.getout.auth;

import com.blue.getout.jwt.JwtService;
import com.blue.getout.user.User;
import com.blue.getout.user.UserDTO;
import com.blue.getout.user.UserRepository;
import com.blue.getout.utils.Mapper;
import com.blue.getout.utils.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final JwtService jwtService;
    private final Utils utils;
    private final Mapper mapper;
    private final JavaMailSender mailSender;

    @Transactional
    public ResponseEntity<AuthenticatedUserDTO> register(RegistrationRequestDTO userDTO,HttpServletResponse response) {
        if (userRepository.existsByName(userDTO.username()) || userRepository.existsByEmail(userDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Username or Email already exists");
        }
        String avatar = utils.getAvatarUrl();
        User user = new User();
        user.setName(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setAvatarUrl(avatar);
        user.setNotifications(new HashSet<>());
        user.setElo(userDTO.elo());
        user.setEmailVerified(false);
        userRepository.save(user);

        String refreshToken = generateRefreshToken(userDTO.username(),response);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user, LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        sendEmail(user.getEmail(), token);

        return this.getUserWithToken(user.getName(),refreshToken);

    }

    public ResponseEntity<String> confirmEmail(String token) {
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        User user = vToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        tokenRepository.delete(vToken);

        return ResponseEntity.ok("Email confirmed");
    }

    public void resendConfirmation(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        // Invalidate existing tokens
        tokenRepository.deleteByUser(user);

        // Create new token
        String token = UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(token,user,LocalDateTime.now().plusHours(24));
        tokenRepository.save(newToken);

        // Send confirmation email
        sendEmail(user.getEmail(), token);
    }
    public ResponseEntity<AuthenticatedUserDTO> login(AuthenticationRequestDTO request, HttpServletResponse response) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        try {
            //Authentication
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Email validation
            User user = userRepository.findByName(request.username())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

            if (!user.isEmailVerified()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified");
            }

            //Generate Token
            String refreshToken = generateRefreshToken(request.username(),response);
            return this.getUserWithToken(request.username(),refreshToken);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        }
    }

    public ResponseEntity<AuthenticatedUserDTO> getMe(final String username,HttpServletResponse response) {
        String refreshToken = generateRefreshToken(username,response);
        return this.getUserWithToken(username,refreshToken);
    }

    public ResponseEntity<AuthenticatedUserDTO> changePassword(String password, Authentication authentication, HttpServletResponse response) {
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        String refreshToken = generateRefreshToken(username, response);
        return getUserWithToken(username,refreshToken);
    }

    public ResponseEntity<AuthenticatedUserDTO>refreshToken(HttpServletRequest request){
        String refreshToken = getRefreshTokenFromCookies(request);

        String username = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        assert refreshToken != null;
        if (!refreshToken.equals(user.getRefreshToken()) || !jwtService.isTokenValid(refreshToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        return getUserWithToken(username, refreshToken);
    }

    private void sendEmail(String to, String token) {
        String url = "https://signsign.azurewebsites.net/confirm-email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirm your email");
        message.setText("Click the link to confirm your email: " + url);
        mailSender.send(message);
    }

    private ResponseEntity<AuthenticatedUserDTO> getUserWithToken(String username, String refreshToken){
        String accessToken = jwtService.generateToken(username);

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in the database"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserDTO userDTO = mapper.UserEntityToDTO(user);

        AuthenticatedUserDTO response = new AuthenticatedUserDTO(userDTO,accessToken);
        return ResponseEntity.ok(response);
    }

    private String generateRefreshToken(String username,HttpServletResponse response){
        String refreshToken = jwtService.generateRefreshToken(username);
        addRefreshTokenToCookie(response, refreshToken);
        return refreshToken;
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true) // change to true for production
                .maxAge(30 * 24 * 60 * 60) // 30 days
                .path("/") // Make the cookie available across the entire domain
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional
    @Scheduled(cron = "@daily")
    public void deleteOldTokens() {
        List<VerificationToken> oldTokens = tokenRepository.findExpiredTokens(LocalDateTime.now());
        if (!oldTokens.isEmpty()) {
            tokenRepository.deleteAll(oldTokens);
        }
    }
}
