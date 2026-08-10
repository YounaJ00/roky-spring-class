package com.example.springboot.post.adapter.in.web.dto;

public record PostResponse(Long id, Long authorId, String title, String content, long viewCount) {}
