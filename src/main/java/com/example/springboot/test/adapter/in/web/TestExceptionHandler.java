package com.example.springboot.test.adapter.in.web;

import com.example.springboot.test.application.TestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TestExceptionHandler {

    @ExceptionHandler(TestNotFoundException.class)
    ResponseEntity<Void> handleNotFound(TestNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
