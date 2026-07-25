package com.example.springboot.test.adapter.in.web;

import com.example.springboot.test.application.TestNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = TestController.class)
class TestExceptionHandler {

    @ExceptionHandler(TestNotFoundException.class)
    ProblemDetail handleNotFound(TestNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /** 도메인 불변식 위반은 클라이언트 입력 오류이므로 400으로 내려보낸다. */
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalidArgument(IllegalArgumentException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
