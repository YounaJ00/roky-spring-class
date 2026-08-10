package com.example.springboot.post.application.port.out;

import com.example.springboot.post.domain.Post;
import java.util.List;

public interface PostQueryPort {

    List<Post> findAll();
}
