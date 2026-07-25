package com.example.springboot.test.application.port.in.dto;

import com.example.springboot.test.Test;

public record CreateTestCommand(String title, String content) {

    public Test toDomain() {
        return Test.create(title, content);
    }
}
