package com.example.springboot.post.application.port.in;

import com.example.springboot.post.application.port.in.dto.PostResult;
import java.util.List;

public interface PostQueryUseCase {

    List<PostResult> getAll();
}
