package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;

public record UpdateTestRequest(String title, String content) {

    public UpdateTestCommand toCommand() {
        return new UpdateTestCommand(title, content);
    }
}
