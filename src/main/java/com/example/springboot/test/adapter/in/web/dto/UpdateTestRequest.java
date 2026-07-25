package com.example.springboot.test.adapter.in.web.dto;

import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import jakarta.validation.constraints.NotBlank;

public record UpdateTestRequest(@NotBlank String title, @NotBlank String content) {

    public UpdateTestCommand toCommand() {
        return new UpdateTestCommand(title, content);
    }
}
