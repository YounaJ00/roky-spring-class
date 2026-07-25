package com.example.springboot.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/** 도메인 클래스 {@code Test}는 JUnit의 {@code @Test} 애노테이션과 단순 이름이 겹치므로, 이 파일에서는 애노테이션을 정규화된 이름으로 사용한다. */
@DisplayName("Test 도메인")
class TestDomainTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-02-03T04:05:06Z");

    @org.junit.jupiter.api.Test
    @DisplayName("create는 제목과 본문을 보존하고 식별자와 감사 시각은 비워 둔다")
    void 새로_생성한_도메인은_식별자와_감사_시각이_비어있다() {
        // Given
        String title = "스프링 수업";
        String content = "헥사고날 아키텍처 정리";

        // When
        Test test = Test.create(title, content);

        // Then
        assertThat(test.getTitle()).isEqualTo(title);
        assertThat(test.getContent()).isEqualTo(content);
        assertThat(test.getId()).as("식별자는 저장 시점에 발급된다").isNull();
        assertThat(test.getCreatedAt()).as("생성 시각은 저장 시점에 채워진다").isNull();
        assertThat(test.getUpdatedAt()).as("수정 시각은 저장 시점에 채워진다").isNull();
    }

    @ParameterizedTest(name = "title=[{0}]")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("create는 제목이 비어 있으면 생성을 거부한다")
    void 제목이_공백이면_생성할_수_없다(String blankTitle) {
        assertThatThrownBy(() -> Test.create(blankTitle, "본문"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
    }

    @ParameterizedTest(name = "content=[{0}]")
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("create는 본문이 비어 있으면 생성을 거부한다")
    void 본문이_공백이면_생성할_수_없다(String blankContent) {
        assertThatThrownBy(() -> Test.create("제목", blankContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("content must not be blank");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("reconstitute는 저장된 다섯 필드를 그대로 복원한다")
    void 저장된_값을_복원하면_모든_필드가_보존된다() {
        // When
        Test test = Test.reconstitute(7L, "제목", "본문", CREATED_AT, UPDATED_AT);

        // Then
        assertThat(test.getId()).isEqualTo(7L);
        assertThat(test.getTitle()).isEqualTo("제목");
        assertThat(test.getContent()).isEqualTo("본문");
        assertThat(test.getCreatedAt()).as("생성 시각과 수정 시각이 뒤바뀌지 않는다").isEqualTo(CREATED_AT);
        assertThat(test.getUpdatedAt()).as("생성 시각과 수정 시각이 뒤바뀌지 않는다").isEqualTo(UPDATED_AT);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("reconstitute는 손상된 저장 데이터를 복원하지 않는다")
    void 저장된_제목이_공백이면_복원할_수_없다() {
        assertThatThrownBy(() -> Test.reconstitute(7L, "  ", "본문", CREATED_AT, UPDATED_AT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("update는 제목과 본문만 바꾸고 식별자와 생성 시각은 유지한다")
    void 수정하면_제목과_본문만_바뀐다() {
        // Given
        Test test = Test.reconstitute(1L, "원래 제목", "원래 본문", CREATED_AT, UPDATED_AT);

        // When
        test.update("새 제목", "새 본문");

        // Then
        assertThat(test.getTitle()).isEqualTo("새 제목");
        assertThat(test.getContent()).isEqualTo("새 본문");
        assertThat(test.getId()).as("식별자는 수정 대상이 아니다").isEqualTo(1L);
        assertThat(test.getCreatedAt()).as("생성 시각은 수정 대상이 아니다").isEqualTo(CREATED_AT);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("update는 제목이 비어 있으면 아무 값도 바꾸지 않는다")
    void 수정할_제목이_공백이면_기존_값이_유지된다() {
        // Given
        Test test = Test.reconstitute(1L, "원래 제목", "원래 본문", CREATED_AT, UPDATED_AT);

        // When & Then
        assertThatThrownBy(() -> test.update("  ", "새 본문"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
        assertThat(test.getTitle()).isEqualTo("원래 제목");
        assertThat(test.getContent()).isEqualTo("원래 본문");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("update는 본문이 비어 있으면 앞서 검증한 제목도 바꾸지 않는다")
    void 수정할_본문이_공백이면_제목도_바뀌지_않는다() {
        // Given
        Test test = Test.reconstitute(1L, "원래 제목", "원래 본문", CREATED_AT, UPDATED_AT);

        // When & Then
        assertThatThrownBy(() -> test.update("새 제목", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("content must not be blank");
        assertThat(test.getTitle()).as("일부만 반영된 상태로 남으면 안 된다").isEqualTo("원래 제목");
        assertThat(test.getContent()).isEqualTo("원래 본문");
    }
}
