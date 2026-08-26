package com.example.springboot.user.application.port.out.dto;

import java.time.Instant;

public record IssuedToken(String value, Instant expiresAt) {}
