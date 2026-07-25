package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import jakarta.validation.constraints.NotBlank;

public record CreateTestRequest(@NotBlank String title, @NotBlank String content) {

    public CreateTestCommand toCommand() {
        return new CreateTestCommand(title, content);
    }
}
