package com.example.springboot.post.application.port.out;

import com.example.springboot.post.domain.Post;
import java.util.Optional;

public interface PostCommandPort {

    Post save(Post post);

    Optional<Post> findById(Long postId);

    Optional<Post> findByIdForUpdate(Long postId);

    void deleteById(Long postId);
}
