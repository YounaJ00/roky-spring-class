package com.example.springboot.user.application.port.out.dto;

import java.util.UUID;

public record TokenClaims(UUID userId, String email) {}
