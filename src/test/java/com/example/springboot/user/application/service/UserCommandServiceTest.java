package com.example.springboot.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.exception.DuplicateEmailException;
import com.example.springboot.user.application.port.in.dto.SignupCommand;
import com.example.springboot.user.application.port.out.AuthSecurityPort;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import com.example.springboot.user.application.port.out.UserCommandPort;
import com.example.springboot.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandService")
class UserCommandServiceTest {

    @Mock private UserCommandPort userCommandPort;

    @Mock private AuthSecurityPort authSecurityPort;

    @Mock private TokenProviderPort tokenProviderPort;

    @InjectMocks private UserCommandService service;

    @Test
    @DisplayName("저장 경쟁 중 이메일 중복이 확인되면 409 정책을 적용한다")
    void 저장_중_이메일이_중복되면_409_정책을_적용한다() {
        // Given
        SignupCommand command = new SignupCommand("duplicate@example.com", "password123");
        given(userCommandPort.existsByEmail(command.email())).willReturn(false);
        given(authSecurityPort.encode(command.password())).willReturn("encoded-password");
        given(userCommandPort.save(any(User.class))).willThrow(new DuplicateEmailException());

        // When & Then
        assertThatThrownBy(() -> service.signup(command))
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> {
                            assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                            assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
                        });
    }
}
