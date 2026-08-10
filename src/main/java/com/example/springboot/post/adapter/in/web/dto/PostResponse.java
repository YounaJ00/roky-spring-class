package com.example.springboot.post.adapter.in.web.dto;

import java.util.UUID;

public record PostResponse(UUID id, Long authorId, String title, String content, long viewCount) {}
