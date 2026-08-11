package com.example.springboot.common.exception;

import java.util.Objects;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(Objects.requireNonNull(errorCode, "errorCode must not be null").message());
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode.status();
    }

    public String getCode() {
        return errorCode.code();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
