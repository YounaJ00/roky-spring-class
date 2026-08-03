package com.example.springboot.post.application.port.out;

import com.example.springboot.post.domain.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryPort {

    Post save(Post post);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdForUpdate(Long postId);

    List<Post> findAll();

    void deleteById(Long postId);
}