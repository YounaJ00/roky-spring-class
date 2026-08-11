package com.example.springboot.user.adapter.in.web;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.common.exception.GlobalExceptionHandler;
import com.example.springboot.user.application.port.in.UserCommandUseCase;
import com.example.springboot.user.application.port.in.UserQueryUseCase;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("사용자 API 입력값 검증")
class UserControllerValidationTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private UserCommandUseCase userCommandUseCase;

    @MockitoBean private UserQueryUseCase userQueryUseCase;

    @MockitoBean private TokenProviderPort tokenProviderPort;

    @MockitoBean private AuthenticationEntryPoint authenticationEntryPoint;

    @Test
    @DisplayName("비밀번호 검증 실패는 필드와 이유만 반환하고 입력값을 노출하지 않는다")
    void 비밀번호_검증_실패는_입력값을_노출하지_않는다() throws Exception {
        // Given
        String requestBody =
                """
                {"email":"valid@example.com","password":"short"}
                """;

        // When & Then
        mockMvc.perform(
                        post("/open-api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("INVALID_INPUT_VALUE"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].reason", not(emptyOrNullString())))
                .andExpect(jsonPath("$.errors[0].value").doesNotExist());
        then(userCommandUseCase).shouldHaveNoInteractions();
    }
}
