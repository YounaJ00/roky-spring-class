package com.example.springboot.post.application.port.in;

import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.in.dto.PostResult;

public interface PostCommandUseCase {

    PostResult create(Long userId, PostCommand command);

    PostResult get(Long postId);

    PostResult update(Long userId, Long postId, PostCommand command);

    void delete(Long userId, Long postId);
}
