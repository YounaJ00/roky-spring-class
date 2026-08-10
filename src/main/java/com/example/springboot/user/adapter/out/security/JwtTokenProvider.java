package com.example.springboot.user.adapter.out.security;

import com.example.springboot.user.application.port.out.TokenProviderPort;
import com.example.springboot.user.application.port.out.dto.IssuedToken;
import com.example.springboot.user.application.port.out.dto.TokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProviderPort {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-millis}") long expirationMillis) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.expirationMillis = expirationMillis;
    }

    @Override
    public IssuedToken issue(Long userId, String email) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expirationMillis);

        String token =
                Jwts.builder()
                        .subject(userId.toString())
                        .claim("email", email)
                        .issuedAt(Date.from(issuedAt))
                        .expiration(Date.from(expiresAt))
                        .signWith(secretKey)
                        .compact();

        return new IssuedToken(token, expiresAt);
    }

    @Override
    public TokenClaims parse(String token) {
        Claims claims =
                Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return new TokenClaims(
                Long.valueOf(claims.getSubject()), claims.get("email", String.class));
    }
}
