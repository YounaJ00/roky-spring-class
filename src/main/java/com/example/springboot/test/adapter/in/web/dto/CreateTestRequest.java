package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.application.port.in.dto.CreateTestCommand;

public record CreateTestRequest(String title, String content) {

    public CreateTestCommand toCommand() {
        return new CreateTestCommand(title, content);
    }
}
