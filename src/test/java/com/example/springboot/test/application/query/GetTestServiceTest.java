package com.example.springboot.test.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetTestService")
class GetTestServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Mock private TestPersistencePort testPersistencePort;

    @InjectMocks private GetTestService getTestService;

    @org.junit.jupiter.api.Test
    @DisplayName("식별자로 조회한 도메인을 반환한다")
    void 존재하는_식별자로_조회한다() {
        // Given
        Test stored = Test.reconstitute(1L, "제목", "본문", CREATED_AT, CREATED_AT);
        given(testPersistencePort.getOrThrow(1L)).willReturn(stored);

        // When
        TestResult result = getTestService.get(1L);

        // Then
        assertThat(result).isEqualTo(new TestResult(1L, "제목", "본문", CREATED_AT, CREATED_AT));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("대상이 없으면 TestNotFoundException을 그대로 전파한다")
    void 없는_식별자로_조회하면_예외가_발생한다() {
        // Given
        willThrow(new TestNotFoundException(999L)).given(testPersistencePort).getOrThrow(999L);

        // When & Then
        assertThatThrownBy(() -> getTestService.get(999L))
                .isInstanceOf(TestNotFoundException.class)
                .hasMessage("Test not found: 999");
    }
}
