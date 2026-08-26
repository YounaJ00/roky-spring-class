package com.example.springboot.user.application.port.in.dto;

import java.time.Instant;

public record LoginResult(String accessToken, Instant expiresAt) {}
