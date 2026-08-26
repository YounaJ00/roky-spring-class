package com.example.springboot.user.application.port.in.dto;

import java.util.UUID;

public record UserResult(UUID id, String email) {}
