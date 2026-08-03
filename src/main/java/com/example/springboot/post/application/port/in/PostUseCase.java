package com.example.springboot.post.application.port.in;

import java.util.List;

public interface PostUseCase {

    PostResult create(Long userId, PostCommand command);

    PostResult get(Long postId);

    List<PostResult> getAll();

    PostResult update(Long userId, Long postId, PostCommand command);

    void delete(Long userId, Long postId);

    record PostCommand(String title, String content) {}

    record PostResult(Long id, Long authorId, String title, String content, long viewCount) {}
}
