package com.example.springboot.post.application.port.in.dto;

import com.example.springboot.post.domain.Post;
import java.util.UUID;

public record PostResult(UUID id, UUID authorId, String title, String content, long viewCount) {

    public static PostResult from(Post post) {
        return new PostResult(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount());
    }
}
