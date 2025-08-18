package com.blue.getout.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserDTO> login(@RequestBody final AuthenticationRequestDTO authenticationRequestDto,  HttpServletResponse response) {
        return authService.login(authenticationRequestDto,response);
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody final RegistrationRequestDTO registrationDTO, HttpServletResponse response) {
       return authService.register(registrationDTO, response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticatedUserDTO> changePassword(@RequestBody PasswordDTO dto,  Authentication authentication,HttpServletResponse response) {
        return authService.changePassword(dto.password(), authentication,response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticatedUserDTO> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserDTO> getMe(final Authentication authentication, HttpServletResponse response) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or user not authenticated. Please sign in again.");
        }
        return authService.getMe(authentication.getName(),response); //getName() returns the email
    }

    @GetMapping("/confirm-email")
    public ResponseEntity<MessageResponse> confirmEmail(@RequestParam String token) {
        return authService.confirmEmail(token);
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<MessageResponse> resendConfirmation(@RequestBody ResendConfirmationRequestDTO request) {
        return authService.resendConfirmation(request.email());
    }
}
