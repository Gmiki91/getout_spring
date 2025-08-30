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
import java.util.Optional;
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
    public ResponseEntity<MessageResponse> register(RegistrationRequestDTO userDTO, HttpServletResponse response) {
        if (userRepository.existsByName(userDTO.username()) || userRepository.existsByEmail(userDTO.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or Email already exists");
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

        String token = generateVerificationToken(user,TokenType.EMAIL_VERIFICATION);

        return sendEmailToConfirm(user.getEmail(), token);
    }

    public ResponseEntity<MessageResponse> confirmEmail(String token) {
        User user = validateToken(token, TokenType.EMAIL_VERIFICATION);
        user.setEmailVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Email confirmed"));
    }

    @Transactional
    public ResponseEntity<MessageResponse> resendConfirmation(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        // Invalidate existing tokens
        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.flush();
        // Create new token
        String token = generateVerificationToken(user,TokenType.EMAIL_VERIFICATION);
        // Send confirmation email
        return sendEmailToConfirm(user.getEmail(), token);
    }

    public ResponseEntity<AuthenticatedUserDTO> login(AuthenticationRequestDTO request, HttpServletResponse response) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password());
        try {
            //Authentication
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //Email validation
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

            if (!user.isEmailVerified()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email not verified");
            }

            //Generate Token
            String refreshToken = generateRefreshToken(request.email(), response);
            return this.getUserWithToken(request.email(), refreshToken);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password", e);
        }
    }

    public ResponseEntity<MessageResponse> forgotPassword(ForgotPasswordRequest request){
        Optional<User> user = userRepository.findByEmail(request.email());
        if (user.isPresent()) {
            String token = generateVerificationToken(user.get(),TokenType.PASSWORD_RESET);
            return sendEmailToResetPassword(user.get().getEmail(), token);
        }
        return ResponseEntity.ok(new MessageResponse("If this email exists, a reset link has been sent."));
    }
    public ResponseEntity<MessageResponse> resetPassword(ResetPasswordRequest request){
        User user = validateToken(request.token(), TokenType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode((request.password())));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password changed"));
    }

    public ResponseEntity<AuthenticatedUserDTO> getMe(final String email, HttpServletResponse response) {
        String refreshToken = generateRefreshToken(email, response);
        return this.getUserWithToken(email, refreshToken);
    }

    public ResponseEntity<AuthenticatedUserDTO> changePassword(String password, Authentication authentication, HttpServletResponse response) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        String refreshToken = generateRefreshToken(email, response);
        return getUserWithToken(email, refreshToken);
    }

    public ResponseEntity<AuthenticatedUserDTO> refreshToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookies(request);

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        assert refreshToken != null;
        if (!refreshToken.equals(user.getRefreshToken()) || !jwtService.isTokenValid(refreshToken, email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        return getUserWithToken(email, refreshToken);
    }

    private ResponseEntity<MessageResponse> sendEmailToConfirm(String to, String token) {
        try {
            String url = "https://signsign.azurewebsites.net/confirm-email?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Confirm your email");
            message.setText("Click the link to confirm your email: " + url);
            mailSender.send(message);
            return ResponseEntity.ok(new MessageResponse("Email sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to send email: " + e.getMessage()));
        }
    }

    private ResponseEntity<MessageResponse> sendEmailToResetPassword(String to, String token) {
        try {
            String url = "https://signsign.azurewebsites.net/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Reset your password");
            message.setText("Click the link to reset your password: " + url);
            mailSender.send(message);
            return ResponseEntity.ok(new MessageResponse("Email sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to send email: " + e.getMessage()));
        }
    }

    private ResponseEntity<AuthenticatedUserDTO> getUserWithToken(String email, String refreshToken) {
        String accessToken = jwtService.generateToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in the database"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserDTO userDTO = mapper.UserEntityToDTO(user);

        AuthenticatedUserDTO response = new AuthenticatedUserDTO(userDTO, accessToken);
        return ResponseEntity.ok(response);
    }

    private String generateVerificationToken(User user, TokenType type){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user,type, LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);
        return token;
    }

    private String generateRefreshToken(String email, HttpServletResponse response) {
        String refreshToken = jwtService.generateRefreshToken(email);
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

    public User validateToken(String token, TokenType expectedType) {
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        if (vToken.getType() != expectedType) {
            throw new RuntimeException("Invalid token type");
        }

        User user = vToken.getUser();
        tokenRepository.delete(vToken);
        return user;
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
