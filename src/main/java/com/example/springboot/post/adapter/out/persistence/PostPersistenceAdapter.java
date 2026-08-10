package com.example.springboot.post.adapter.out.persistence;

import com.example.springboot.post.application.port.out.PostCommandPort;
import com.example.springboot.post.application.port.out.PostQueryPort;
import com.example.springboot.post.domain.Post;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostCommandPort, PostQueryPort {

    private final PostJpaRepository repository;

    @Override
    public Post save(Post post) {
        return repository.save(PostJpaEntity.from(post)).toDomain();
    }

    @Override
    public Optional<Post> findById(UUID postId) {
        return repository.findById(postId).map(PostJpaEntity::toDomain);
    }

    @Override
    public Optional<Post> findByIdForUpdate(UUID postId) {
        return repository.findByIdForUpdate(postId).map(PostJpaEntity::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostJpaEntity::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID postId) {
        repository.deleteById(postId);
    }
}
