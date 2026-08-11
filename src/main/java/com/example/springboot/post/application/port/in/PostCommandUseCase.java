package com.example.springboot.post.application.port.in;

import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.in.dto.PostResult;
import java.util.UUID;

public interface PostCommandUseCase {

    PostResult create(UUID userId, PostCommand command);

    PostResult get(UUID postId);

    PostResult update(UUID userId, UUID postId, PostCommand command);

    void delete(UUID userId, UUID postId);
}
