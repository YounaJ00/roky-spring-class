package com.example.springboot.post.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.post.application.exception.PostErrorCode;
import com.example.springboot.post.application.port.in.PostCommandUseCase;
import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.in.dto.PostResult;
import com.example.springboot.post.application.port.out.PostCommandPort;
import com.example.springboot.post.domain.Post;
import com.example.springboot.post.domain.exception.PostAuthorMismatchException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService implements PostCommandUseCase {

    private final PostCommandPort postCommandPort;

    @Override
    public PostResult create(UUID userId, PostCommand command) {
        return PostResult.from(
                postCommandPort.save(Post.create(userId, command.title(), command.content())));
    }

    @Override
    public PostResult get(UUID postId) {
        Post post =
                postCommandPort
                        .findByIdForUpdate(postId)
                        .orElseThrow(() -> new ApiException(PostErrorCode.POST_NOT_FOUND));

        post.increaseViewCount();

        return PostResult.from(postCommandPort.save(post));
    }

    @Override
    public PostResult update(UUID userId, UUID postId, PostCommand command) {
        Post post = findPost(postId);

        try {
            post.update(userId, command.title(), command.content());
        } catch (PostAuthorMismatchException exception) {
            throw new ApiException(PostErrorCode.POST_AUTHOR_REQUIRED);
        }

        return PostResult.from(postCommandPort.save(post));
    }

    @Override
    public void delete(UUID userId, UUID postId) {
        Post post = findPost(postId);

        validateAuthor(post, userId);

        postCommandPort.deleteById(postId);
    }

    private Post findPost(UUID postId) {
        return postCommandPort
                .findById(postId)
                .orElseThrow(() -> new ApiException(PostErrorCode.POST_NOT_FOUND));
    }

    private void validateAuthor(Post post, UUID userId) {
        try {
            post.validateAuthor(userId);
        } catch (PostAuthorMismatchException exception) {
            throw new ApiException(PostErrorCode.POST_AUTHOR_REQUIRED);
        }
    }
}
