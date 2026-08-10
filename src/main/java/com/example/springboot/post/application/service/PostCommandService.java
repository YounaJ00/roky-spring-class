package com.example.springboot.post.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.post.application.port.in.PostCommandUseCase;
import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.in.dto.PostResult;
import com.example.springboot.post.application.port.out.PostCommandPort;
import com.example.springboot.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommandService implements PostCommandUseCase {

    private final PostCommandPort postCommandPort;

    @Override
    public PostResult create(Long userId, PostCommand command) {
        return PostResult.from(
                postCommandPort.save(Post.create(userId, command.title(), command.content())));
    }

    @Override
    public PostResult get(Long postId) {
        Post post =
                postCommandPort
                        .findByIdForUpdate(postId)
                        .orElseThrow(
                                () -> new ApiException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));

        post.increaseViewCount();

        return PostResult.from(postCommandPort.save(post));
    }

    @Override
    public PostResult update(Long userId, Long postId, PostCommand command) {
        Post post = findPost(postId);

        validateAuthor(post, userId);

        post.update(command.title(), command.content());

        return PostResult.from(postCommandPort.save(post));
    }

    @Override
    public void delete(Long userId, Long postId) {
        Post post = findPost(postId);

        validateAuthor(post, userId);

        postCommandPort.deleteById(postId);
    }

    private Post findPost(Long postId) {
        return postCommandPort
                .findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));
    }

    private void validateAuthor(Post post, Long userId) {
        if (!post.getAuthorId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "작성자만 변경할 수 있습니다.");
        }
    }
}
