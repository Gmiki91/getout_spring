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
        userRepository.save(user);
        String refreshToken = generateRefreshToken(userDTO.username(),response);
        return this.getUserWithToken(user.getName(),refreshToken);

    }
    public ResponseEntity<AuthenticatedUserDTO> login(AuthenticationRequestDTO request, HttpServletResponse response) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        try {
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
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
}
