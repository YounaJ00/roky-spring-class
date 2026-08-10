package com.example.springboot.post.application.service;

import com.example.springboot.post.application.port.in.PostQueryUseCase;
import com.example.springboot.post.application.port.in.dto.PostResult;
import com.example.springboot.post.application.port.out.PostQueryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostQueryService implements PostQueryUseCase {

    private final PostQueryPort postQueryPort;

    @Override
    public List<PostResult> getAll() {
        return postQueryPort.findAll().stream().map(PostResult::from).toList();
    }
}
