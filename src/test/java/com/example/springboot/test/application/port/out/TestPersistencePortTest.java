package com.example.springboot.test.application.port.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;

@DisplayName("TestPersistencePort.getOrThrow")
class TestPersistencePortTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

    /** 조회 결과만 바꿔 끼우는 최소 구현. 나머지 연산은 이 테스트의 관심사가 아니다. */
    private static TestPersistencePort portReturning(Optional<Test> found) {
        return new TestPersistencePort() {
            @Override
            public Test save(Test test) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Optional<Test> findById(Long id) {
                return found;
            }

            @Override
            public List<Test> findAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteById(Long id) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @org.junit.jupiter.api.Test
    @DisplayName("조회에 성공하면 도메인을 그대로 돌려준다")
    void 조회에_성공하면_도메인을_반환한다() {
        // Given
        Test stored = Test.reconstitute(1L, "제목", "본문", CREATED_AT, CREATED_AT);

        // When
        Test found = portReturning(Optional.of(stored)).getOrThrow(1L);

        // Then
        assertThat(found).isSameAs(stored);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("조회 결과가 없으면 식별자를 담은 TestNotFoundException을 던진다")
    void 조회_결과가_없으면_예외를_던진다() {
        assertThatThrownBy(() -> portReturning(Optional.empty()).getOrThrow(999L))
                .isInstanceOf(TestNotFoundException.class)
                .hasMessage("Test not found: 999");
    }
}
