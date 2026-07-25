package com.example.springboot.test.application.port.in.dto;

import com.example.springboot.test.Test;

public record UpdateTestCommand(String title, String content) {

    public void applyTo(Test test) {
        test.update(title, content);
    }
}
