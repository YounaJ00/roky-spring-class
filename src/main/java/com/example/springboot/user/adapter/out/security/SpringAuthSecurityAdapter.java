package com.example.springboot.user.adapter.out.security;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.user.application.port.out.AuthSecurityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAuthSecurityAdapter implements AuthSecurityPort {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public AuthenticatedUser authenticate(String email, String rawPassword) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken.unauthenticated(
                                    email, rawPassword));

            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

            return new AuthenticatedUser(principal.getUserId(), principal.getEmail());

        } catch (AuthenticationException exception) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
