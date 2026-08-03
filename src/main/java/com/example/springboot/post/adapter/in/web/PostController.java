package com.example.springboot.post.adapter.in.web;

import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.post.application.port.in.PostUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostUseCase postUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostUseCase.PostResult create(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody PostRequest request) {
        return postUseCase.create(principal.getUserId(), request.toCommand());
    }

    @GetMapping("/{postId}")
    public PostUseCase.PostResult get(@PathVariable Long postId) {
        return postUseCase.get(postId);
    }

    @GetMapping
    public List<PostUseCase.PostResult> getAll() {
        return postUseCase.getAll();
    }

    @PatchMapping("/{postId}")
    public PostUseCase.PostResult update(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request) {
        return postUseCase.update(principal.getUserId(), postId, request.toCommand());
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long postId) {

        postUseCase.delete(principal.getUserId(), postId);
    }

    public record PostRequest(@NotBlank String title, @NotBlank String content) {
        private PostUseCase.PostCommand toCommand() {
            return new PostUseCase.PostCommand(title, content);
        }
    }
}
