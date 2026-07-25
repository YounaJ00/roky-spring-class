package com.example.springboot.test.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateTestService")
class CreateTestServiceTest {

    private static final Instant SAVED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Mock private TestPersistencePort testPersistencePort;

    @InjectMocks private CreateTestService createTestService;

    @Captor private ArgumentCaptor<Test> savedTestCaptor;

    @org.junit.jupiter.api.Test
    @DisplayName("생성 명령을 도메인으로 변환해 저장하고 저장 결과를 반환한다")
    void 생성_명령을_도메인으로_변환해_저장한다() {
        // Given
        CreateTestCommand command = new CreateTestCommand("스프링 수업", "헥사고날 아키텍처 정리");
        Test saved = Test.reconstitute(1L, "스프링 수업", "헥사고날 아키텍처 정리", SAVED_AT, SAVED_AT);
        given(testPersistencePort.save(any(Test.class))).willReturn(saved);

        // When
        TestResult result = createTestService.create(command);

        // Then
        then(testPersistencePort).should().save(savedTestCaptor.capture());
        Test requestedToSave = savedTestCaptor.getValue();
        assertThat(requestedToSave.getTitle()).isEqualTo("스프링 수업");
        assertThat(requestedToSave.getContent()).isEqualTo("헥사고날 아키텍처 정리");
        assertThat(requestedToSave.getId()).as("식별자 발급은 영속 계층 책임이다").isNull();
        assertThat(result)
                .as("저장 계층이 돌려준 도메인이 결과에 그대로 담긴다")
                .isEqualTo(new TestResult(1L, "스프링 수업", "헥사고날 아키텍처 정리", SAVED_AT, SAVED_AT));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("제목이 비어 있으면 저장을 시도하지 않는다")
    void 제목이_공백이면_저장하지_않는다() {
        // Given
        CreateTestCommand command = new CreateTestCommand("  ", "헥사고날 아키텍처 정리");

        // When & Then
        assertThatThrownBy(() -> createTestService.create(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
        then(testPersistencePort).shouldHaveNoInteractions();
    }
}
