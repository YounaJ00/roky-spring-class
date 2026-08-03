package com.example.springboot.user.application.port.out;

public interface AuthSecurityPort {
    String encode(String rawPassword);

    AuthenticatedUser authenticate(String email, String rawPassword);

    record AuthenticatedUser(Long userId, String email) {}
}
