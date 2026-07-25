package com.example.springboot.test.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteTestService")
class DeleteTestServiceTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    @Mock private TestPersistencePort testPersistencePort;

    @InjectMocks private DeleteTestService deleteTestService;

    @org.junit.jupiter.api.Test
    @DisplayName("존재를 확인한 뒤 식별자로 삭제한다")
    void 존재를_확인한_뒤_삭제한다() {
        // Given
        Test stored = Test.reconstitute(1L, "제목", "본문", CREATED_AT, CREATED_AT);
        given(testPersistencePort.getOrThrow(1L)).willReturn(stored);

        // When
        deleteTestService.delete(1L);

        // Then
        then(testPersistencePort).should().deleteById(1L);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("대상이 없으면 TestNotFoundException을 던지고 삭제하지 않는다")
    void 없는_식별자를_삭제하면_예외가_발생한다() {
        // Given
        willThrow(new TestNotFoundException(999L)).given(testPersistencePort).getOrThrow(999L);

        // When & Then
        assertThatThrownBy(() -> deleteTestService.delete(999L))
                .isInstanceOf(TestNotFoundException.class)
                .hasMessage("Test not found: 999");
        then(testPersistencePort).should(Mockito.never()).deleteById(Mockito.anyLong());
    }
}
