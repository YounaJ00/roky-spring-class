package com.example.springboot.post.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(@NotBlank String title, @NotBlank String content) {}
