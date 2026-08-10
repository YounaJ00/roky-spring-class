package com.example.springboot.post.application.port.out;

import com.example.springboot.post.domain.Post;
import java.util.Optional;
import java.util.UUID;

public interface PostCommandPort {

    Post save(Post post);

    Optional<Post> findById(UUID postId);

    Optional<Post> findByIdForUpdate(UUID postId);

    void deleteById(UUID postId);
}
