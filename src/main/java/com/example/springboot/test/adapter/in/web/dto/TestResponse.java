package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.application.port.in.dto.TestResult;
import java.time.Instant;

public record TestResponse(
        Long id, String title, String content, Instant createdAt, Instant updatedAt) {

    public static TestResponse from(TestResult result) {
        return new TestResponse(
                result.id(),
                result.title(),
                result.content(),
                result.createdAt(),
                result.updatedAt());
    }
}
