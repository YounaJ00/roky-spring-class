package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.Test;
import java.time.Instant;

public record TestResponse(
        Long id, String title, String content, Instant createdAt, Instant updatedAt) {

    public static TestResponse from(Test test) {
        return new TestResponse(
                test.getId(),
                test.getTitle(),
                test.getContent(),
                test.getCreatedAt(),
                test.getUpdatedAt());
    }
}
