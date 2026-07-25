package com.example.springboot.test.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot.test.Test;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;

@DisplayName("TestJpaEntity 매핑")
class TestJpaEntityMappingTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-02-03T04:05:06Z");

    @org.junit.jupiter.api.Test
    @DisplayName("도메인에서 엔티티로 갔다가 돌아와도 다섯 필드가 모두 보존된다")
    void 도메인과_엔티티_사이를_왕복해도_값이_보존된다() {
        // Given
        Test origin = Test.reconstitute(42L, "제목", "본문", CREATED_AT, UPDATED_AT);

        // When
        Test restored = TestJpaEntity.from(origin).toDomain();

        // Then
        assertThat(restored.getId()).isEqualTo(42L);
        assertThat(restored.getTitle()).isEqualTo("제목");
        assertThat(restored.getContent()).isEqualTo("본문");
        assertThat(restored.getCreatedAt()).as("생성 시각과 수정 시각이 뒤바뀌지 않는다").isEqualTo(CREATED_AT);
        assertThat(restored.getUpdatedAt()).as("생성 시각과 수정 시각이 뒤바뀌지 않는다").isEqualTo(UPDATED_AT);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("아직 저장되지 않은 도메인은 식별자와 감사 시각이 비어 있는 채로 매핑된다")
    void 저장_전_도메인도_매핑할_수_있다() {
        // Given
        Test created = Test.create("제목", "본문");

        // When
        Test restored = TestJpaEntity.from(created).toDomain();

        // Then
        assertThat(restored.getTitle()).isEqualTo("제목");
        assertThat(restored.getContent()).isEqualTo("본문");
        assertThat(restored.getId()).isNull();
        assertThat(restored.getCreatedAt()).as("감사 시각은 영속화 시점에 채워진다").isNull();
        assertThat(restored.getUpdatedAt()).as("감사 시각은 영속화 시점에 채워진다").isNull();
    }
}
