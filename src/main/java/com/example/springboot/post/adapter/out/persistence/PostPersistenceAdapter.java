package com.example.springboot.post.adapter.out.persistence;

import com.example.springboot.post.application.port.out.PostRepositoryPort;
import com.example.springboot.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter
        implements PostRepositoryPort {

    private final PostJpaRepository repository;

    @Override
    public Post save(Post post) {
        PostJpaEntity saved = repository.save(
                PostJpaEntity.of(
                        post.getId(),
                        post.getAuthorId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViewCount()
                )
        );

        return toDomain(saved);
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return repository.findById(postId)
                .map(this::toDomain);
    }

    @Override
    public Optional<Post> findByIdForUpdate(Long postId) {
        return repository.findByIdForUpdate(postId)
                .map(this::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return repository.findAllByOrderByIdDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long postId) {
        repository.deleteById(postId);
    }

    private Post toDomain(PostJpaEntity entity) {
        return Post.restore(
                entity.getId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewCount()
        );
    }
}