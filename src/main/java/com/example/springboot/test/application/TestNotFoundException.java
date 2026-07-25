package com.example.springboot.test.application;

public class TestNotFoundException extends RuntimeException {

    public TestNotFoundException(Long id) {
        super("Test not found: " + id);
    }
}
