package com.example.springboot.user.application.port.out;

import java.time.Instant;

public interface TokenProviderPort {
    IssuedToken issue(Long userId, String email);

    TokenClaims parse(String token);

    record IssuedToken(String value, Instant expiresAt) {}

    record TokenClaims(Long userId, String email) {}
}
