package com.example.springboot.user.adapter.in.web.dto;

import java.time.Instant;

public record LoginResponse(String accessToken, Instant expiresAt) {}
