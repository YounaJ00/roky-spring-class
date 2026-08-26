package com.example.springboot.common.exception;

/** 입력값 검증 실패 위치와 이유만 제공하며, 거절된 실제 입력값은 민감 정보 보호를 위해 포함하지 않는다. */
public record FieldValidationError(String field, String reason) {}
