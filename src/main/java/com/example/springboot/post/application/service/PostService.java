package com.example.springboot.post.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.post.application.port.in.PostUseCase;
import com.example.springboot.post.application.port.out.PostRepositoryPort;
import com.example.springboot.post.domain.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService implements PostUseCase {

    private final PostRepositoryPort postRepositoryPort;

    @Override
    @Transactional
    public PostResult create(Long userId, PostCommand command) {
        Post saved =
                postRepositoryPort.save(Post.create(userId, command.title(), command.content()));

        return toResult(saved);
    }

    @Override
    @Transactional
    public PostResult get(Long postId) {
        Post post =
                postRepositoryPort
                        .findByIdForUpdate(postId)
                        .orElseThrow(
                                () -> new ApiException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));

        post.increaseViewCount();

        return toResult(postRepositoryPort.save(post));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResult> getAll() {
        return postRepositoryPort.findAll().stream().map(this::toResult).toList();
    }

    @Override
    @Transactional
    public PostResult update(Long userId, Long postId, PostCommand command) {
        Post post = findPost(postId);

        validateAuthor(post, userId);

        post.update(command.title(), command.content());

        return toResult(postRepositoryPort.save(post));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long postId) {
        Post post = findPost(postId);

        validateAuthor(post, userId);

        postRepositoryPort.deleteById(postId);
    }

    private Post findPost(Long postId) {
        return postRepositoryPort
                .findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "게시물을 찾을 수 없습니다."));
    }

    private void validateAuthor(Post post, Long userId) {
        if (!post.getAuthorId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "작성자만 변경할 수 있습니다.");
        }
    }

    private PostResult toResult(Post post) {
        return new PostResult(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount());
    }
}
