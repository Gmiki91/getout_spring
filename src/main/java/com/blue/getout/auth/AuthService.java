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
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Utils utils;
    private final Mapper mapper;

    @Transactional
    public ResponseEntity<AuthenticatedUserDTO> register(RegistrationRequestDTO userDTO) {
        if (userRepository.existsByName(userDTO.username()) || userRepository.existsByEmail(userDTO.email())) {
            throw new ValidationException("Username or Email already exists");
        }
        String avatar = utils.getAvatarUrl();
        User user = new User();
        user.setName(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setAvatarUrl(avatar);
        user.setNotifications(new HashSet<>());
        user.setElo(userDTO.elo());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(getUserWithToken(user.getName()).getBody());

    }
    public ResponseEntity<AuthenticatedUserDTO> login(AuthenticationRequestDTO request, HttpServletResponse response) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String refreshToken = jwtService.generateRefreshToken(request.username());
            addRefreshTokenToCookie(response, refreshToken);
            return this.getUserWithToken(request.username());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        }
    }

    public ResponseEntity<AuthenticatedUserDTO> changePassword(String password, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return getUserWithToken(user.getName());
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

        return getUserWithToken(username);
    }

    private ResponseEntity<AuthenticatedUserDTO> getUserWithToken(String username){
        String accessToken = jwtService.generateToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        User user = userRepository.findByName(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in the database"));

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserDTO userDTO = mapper.UserEntityToDTO(user);

        AuthenticatedUserDTO response = new AuthenticatedUserDTO(userDTO,accessToken,refreshToken);
        return ResponseEntity.ok(response);
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
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true); // Ensure the cookie is only accessible by the backend
        cookie.setSecure(true); // Ensure the cookie is only sent over HTTPS
        cookie.setPath("/"); // Make the cookie available across the entire domain
        cookie.setMaxAge(30 * 24 * 60 * 60); // Set expiration for 30 days
        response.addCookie(cookie);
    }


}
