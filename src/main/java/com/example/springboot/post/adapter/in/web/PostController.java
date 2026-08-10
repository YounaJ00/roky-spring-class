package com.example.springboot.post.adapter.in.web;

import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.post.adapter.in.web.dto.PostRequest;
import com.example.springboot.post.adapter.in.web.dto.PostResponse;
import com.example.springboot.post.application.port.in.PostUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostUseCase postUseCase;

    @PostMapping("/api/v1/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody PostRequest request) {
        return PostMapper.toResponse(
                postUseCase.create(principal.getUserId(), PostMapper.toCommand(request)));
    }

    @GetMapping("/open-api/v1/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return PostMapper.toResponse(postUseCase.get(postId));
    }

    @GetMapping("/open-api/v1/posts")
    public List<PostResponse> getAll() {
        return PostMapper.toResponses(postUseCase.getAll());
    }

    @PatchMapping("/api/v1/posts/{postId}")
    public PostResponse update(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request) {
        return PostMapper.toResponse(
                postUseCase.update(principal.getUserId(), postId, PostMapper.toCommand(request)));
    }

    @DeleteMapping("/api/v1/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long postId) {

        postUseCase.delete(principal.getUserId(), postId);
    }
}
