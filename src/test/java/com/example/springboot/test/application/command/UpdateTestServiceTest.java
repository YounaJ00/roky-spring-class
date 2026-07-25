package com.example.springboot.test.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateTestService")
class UpdateTestServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Mock private TestPersistencePort testPersistencePort;

    @InjectMocks private UpdateTestService updateTestService;

    @org.junit.jupiter.api.Test
    @DisplayName("기존 도메인의 제목과 본문을 바꿔 저장하고 변경된 결과를 반환한다")
    void 조회한_도메인에_새_값을_반영해_저장한다() {
        // Given
        Test stored = Test.reconstitute(1L, "원래 제목", "원래 본문", CREATED_AT, CREATED_AT);
        given(testPersistencePort.getOrThrow(1L)).willReturn(stored);
        given(testPersistencePort.save(any(Test.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // When
        TestResult result = updateTestService.update(1L, new UpdateTestCommand("새 제목", "새 본문"));

        // Then
        assertThat(result.title()).isEqualTo("새 제목");
        assertThat(result.content()).isEqualTo("새 본문");
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.createdAt()).as("생성 시각은 수정으로 바뀌지 않는다").isEqualTo(CREATED_AT);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("대상이 없으면 TestNotFoundException을 던지고 저장하지 않는다")
    void 없는_식별자를_수정하면_예외가_발생한다() {
        // Given
        willThrow(new TestNotFoundException(999L)).given(testPersistencePort).getOrThrow(999L);

        // When & Then
        assertThatThrownBy(
                        () -> updateTestService.update(999L, new UpdateTestCommand("새 제목", "새 본문")))
                .isInstanceOf(TestNotFoundException.class)
                .hasMessage("Test not found: 999");
        then(testPersistencePort).should(org.mockito.Mockito.never()).save(any(Test.class));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("본문이 비어 있으면 저장하지 않고 조회한 도메인도 그대로 남는다")
    void 본문이_공백이면_저장하지_않는다() {
        // Given
        Test stored = Test.reconstitute(1L, "원래 제목", "원래 본문", CREATED_AT, CREATED_AT);
        given(testPersistencePort.getOrThrow(1L)).willReturn(stored);

        // When & Then
        assertThatThrownBy(() -> updateTestService.update(1L, new UpdateTestCommand("새 제목", "  ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("content must not be blank");
        assertThat(stored.getTitle()).as("검증에 실패하면 어떤 필드도 바뀌지 않는다").isEqualTo("원래 제목");
        assertThat(stored.getContent()).isEqualTo("원래 본문");
        then(testPersistencePort).should(org.mockito.Mockito.never()).save(any(Test.class));
    }
}
