package com.blue.getout.auth;

import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthenticatedUserDTO> login(@RequestBody final AuthenticationRequestDTO authenticationRequestDto) {
        return authService.login(authenticationRequestDto);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticatedUserDTO> registerUser(@Valid @RequestBody final RegistrationRequestDTO registrationDTO) {
       return authService.register(registrationDTO);
    }

}
