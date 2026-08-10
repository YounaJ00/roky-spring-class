package com.example.springboot.user.application.port.out;

import com.example.springboot.user.application.port.out.dto.IssuedToken;
import com.example.springboot.user.application.port.out.dto.TokenClaims;

public interface TokenProviderPort {
    IssuedToken issue(Long userId, String email);

    TokenClaims parse(String token);
}
