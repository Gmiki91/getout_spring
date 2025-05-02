package com.blue.getout.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<AuthenticatedUserDTO> registerUser(@Valid @RequestBody final RegistrationRequestDTO registrationDTO) {
       return authService.register(registrationDTO);
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticatedUserDTO> changePassword(@RequestBody PasswordDTO dto,  Authentication authentication) {
        return authService.changePassword(dto.password(), authentication);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticatedUserDTO> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

}
