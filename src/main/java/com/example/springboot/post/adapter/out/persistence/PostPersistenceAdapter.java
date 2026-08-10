package com.example.springboot.post.adapter.out.persistence;

import com.example.springboot.post.application.port.out.PostRepositoryPort;
import com.example.springboot.post.domain.Post;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements PostRepositoryPort {

    private final PostJpaRepository repository;

    @Override
    public Post save(Post post) {
        return repository.save(PostJpaEntity.from(post)).toDomain();
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return repository.findById(postId).map(PostJpaEntity::toDomain);
    }

    @Override
    public Optional<Post> findByIdForUpdate(Long postId) {
        return repository.findByIdForUpdate(postId).map(PostJpaEntity::toDomain);
    }

    @Override
    public List<Post> findAll() {
        return repository.findAllByOrderByIdDesc().stream().map(PostJpaEntity::toDomain).toList();
    }

    @Override
    public void deleteById(Long postId) {
        repository.deleteById(postId);
    }
}
