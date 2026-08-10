package com.example.springboot.post.domain;

import java.util.UUID;
import lombok.Getter;

@Getter
public class Post {

    private final UUID id;
    private final Long authorId;

    private String title;
    private String content;
    private long viewCount;

    private Post(UUID id, Long authorId, String title, String content, long viewCount) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public static Post create(Long authorId, String title, String content) {
        return new Post(null, authorId, title, content, 0L);
    }

    public static Post restore(
            UUID id, Long authorId, String title, String content, long viewCount) {
        return new Post(id, authorId, title, content, viewCount);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        viewCount++;
    }
}
