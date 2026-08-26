package com.example.springboot.user.application.port.out;

import com.example.springboot.user.application.port.out.dto.AuthenticatedUser;

public interface AuthSecurityPort {
    String encode(String rawPassword);

    AuthenticatedUser authenticate(String email, String rawPassword);
}
