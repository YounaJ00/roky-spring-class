package com.example.springboot.common.exception;

import java.util.List;

/**
 * 비즈니스 오류와 DTO 검증 오류를 동일한 API 규격으로 반환한다. 일반 오류의 errors는 빈 목록이며, 검증 오류에는 field와 reason만 포함해
 * 비밀번호·개인정보가 될 수 있는 실패 입력값을 노출하지 않는다.
 */
public record ErrorResponse(
        int status, String error, String message, List<FieldValidationError> errors) {

    public ErrorResponse {
        errors = List.copyOf(errors);
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return from(errorCode, List.of());
    }

    public static ErrorResponse from(
            ErrorCode errorCode, List<FieldValidationError> validationErrors) {
        return new ErrorResponse(
                errorCode.status().value(),
                errorCode.code(),
                errorCode.message(),
                validationErrors);
    }
}
