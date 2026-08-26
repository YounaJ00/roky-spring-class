package com.example.springboot.user.application.port.out.dto;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String email) {}
