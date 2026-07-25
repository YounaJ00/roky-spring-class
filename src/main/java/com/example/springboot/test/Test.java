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

    public void update(String title, String content) {
        this.title = requireText(title, "title");
        this.content = requireText(content, "content");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
