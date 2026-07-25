package com.example.springboot.test;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Test {

    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;

    public static Test create(String title, String content) {
        return new Test(
                null, requireText(title, "title"), requireText(content, "content"), null, null);
    }

    public static Test reconstitute(
            Long id, String title, String content, Instant createdAt, Instant updatedAt) {
        return new Test(
                id,
                requireText(title, "title"),
                requireText(content, "content"),
                createdAt,
                updatedAt);
    }

    /**
     * 제목과 본문을 함께 변경합니다. 둘 중 하나라도 비어 있으면 어떤 값도 바뀌지 않습니다.
     *
     * @param title 새 제목, 공백일 수 없음
     * @param content 새 본문, 공백일 수 없음
     * @throws IllegalArgumentException 제목 또는 본문이 공백인 경우
     */
    public void update(String title, String content) {
        String validatedTitle = requireText(title, "title");
        String validatedContent = requireText(content, "content");
        this.title = validatedTitle;
        this.content = validatedContent;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
