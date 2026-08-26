package com.example.springboot.user.application.port.out;

import com.example.springboot.user.application.port.out.dto.IssuedToken;
import com.example.springboot.user.application.port.out.dto.TokenClaims;
import java.util.UUID;

public interface TokenProviderPort {
    IssuedToken issue(UUID userId, String email);

    TokenClaims parse(String token);
}
