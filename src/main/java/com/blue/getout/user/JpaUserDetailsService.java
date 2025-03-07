package com.blue.getout.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {

        return userRepository.findByName(username).map(user ->
                User.builder()
                        .username(username)
                        .password(user.getPassword())
                        .build()
        ).orElseThrow(() -> new UsernameNotFoundException("User with username [%s] not found".formatted(username)));
    }
}