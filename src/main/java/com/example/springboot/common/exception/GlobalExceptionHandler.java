package com.example.springboot.common.exception;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(ErrorResponse.from(exception.getErrorCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        // 거절된 입력값은 비밀번호나 개인정보일 수 있으므로 field와 reason만 응답한다.
        List<FieldValidationError> validationErrors =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(
                                fieldError ->
                                        new FieldValidationError(
                                                fieldError.getField(),
                                                fieldError.getDefaultMessage()))
                        .toList();

        return ResponseEntity.status(ValidationErrorCode.INVALID_INPUT_VALUE.status())
                .body(
                        ErrorResponse.from(
                                ValidationErrorCode.INVALID_INPUT_VALUE, validationErrors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception) {
        return ResponseEntity.status(ValidationErrorCode.BAD_REQUEST.status())
                .body(ErrorResponse.from(ValidationErrorCode.BAD_REQUEST));
    }
}
