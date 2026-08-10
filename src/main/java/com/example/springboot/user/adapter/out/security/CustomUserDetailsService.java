package com.example.springboot.user.adapter.out.security;

import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserQueryPort userQueryPort;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user =
                userQueryPort
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(email));

        return new CustomUserPrincipal(user.getId(), user.getEmail(), user.getEncodedPassword());
    }
}
