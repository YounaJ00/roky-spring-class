package com.example.springboot.test.application.port.in.dto;

import com.example.springboot.test.Test;
import java.time.Instant;

/** 유즈케이스 실행 결과. 도메인을 어댑터로 그대로 내보내지 않기 위한 경계 타입이다. */
public record TestResult(
        Long id, String title, String content, Instant createdAt, Instant updatedAt) {

    public static TestResult from(Test test) {
        return new TestResult(
                test.getId(),
                test.getTitle(),
                test.getContent(),
                test.getCreatedAt(),
                test.getUpdatedAt());
    }
}
