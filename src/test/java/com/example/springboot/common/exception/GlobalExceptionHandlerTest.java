package com.example.springboot.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot.post.application.exception.PostErrorCode;
import com.example.springboot.user.application.exception.AuthErrorCode;
import com.example.springboot.user.application.exception.UserErrorCode;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("잘못된 인자 예외는 기존 BAD_REQUEST 응답을 유지한다")
    void 잘못된_인자_예외는_BAD_REQUEST를_응답한다() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("invalid");

        // When
        ResponseEntity<ErrorResponse> response =
                handler.handleIllegalArgumentException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .isEqualTo(
                        new ErrorResponse(
                                400, "BAD_REQUEST", "입력값이 올바르지 않습니다.", List.of()));
    }

    @ParameterizedTest
    @MethodSource("apiErrorCodes")
    @DisplayName("API 예외의 상태와 도메인 오류 코드와 메시지를 응답한다")
    void API_예외를_오류_응답으로_변환한다(
            ErrorCode errorCode, HttpStatus expectedStatus, String expectedCode, String message) {
        // Given
        ApiException exception = new ApiException(errorCode);

        // When
        ResponseEntity<ErrorResponse> response = handler.handleApiException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(response.getBody())
                .isEqualTo(
                        new ErrorResponse(
                                expectedStatus.value(), expectedCode, message, List.of()));
    }

    private static Stream<Arguments> apiErrorCodes() {
        return Stream.of(
                Arguments.of(
                        UserErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND",
                        "사용자를 찾을 수 없습니다."),
                Arguments.of(
                        UserErrorCode.EMAIL_ALREADY_EXISTS,
                        HttpStatus.CONFLICT,
                        "EMAIL_ALREADY_EXISTS",
                        "이미 사용 중인 이메일입니다."),
                Arguments.of(
                        AuthErrorCode.INVALID_CREDENTIALS,
                        HttpStatus.UNAUTHORIZED,
                        "INVALID_CREDENTIALS",
                        "이메일 또는 비밀번호가 올바르지 않습니다."),
                Arguments.of(
                        PostErrorCode.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "POST_NOT_FOUND",
                        "게시물을 찾을 수 없습니다."),
                Arguments.of(
                        PostErrorCode.POST_AUTHOR_REQUIRED,
                        HttpStatus.FORBIDDEN,
                        "POST_AUTHOR_REQUIRED",
                        "작성자만 변경할 수 있습니다."));
    }
}
