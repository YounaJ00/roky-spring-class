package com.example.springboot.user.adapter.out.security;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.user.application.exception.AuthErrorCode;
import com.example.springboot.user.application.port.out.AuthSecurityPort;
import com.example.springboot.user.application.port.out.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
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

            if (!(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
                throw new ApiException(AuthErrorCode.INVALID_CREDENTIALS);
            }

            return new AuthenticatedUser(principal.getUserId(), principal.getEmail());

        } catch (AuthenticationException exception) {
            throw new ApiException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }
}
