package com.example.springboot.post.adapter.in.web;

import com.example.springboot.post.adapter.in.web.dto.PostRequest;
import com.example.springboot.post.adapter.in.web.dto.PostResponse;
import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.in.dto.PostResult;
import java.util.List;

public final class PostMapper {

    private PostMapper() {}

    public static PostCommand toCommand(PostRequest request) {
        return new PostCommand(request.title(), request.content());
    }

    public static PostResponse toResponse(PostResult result) {
        return new PostResponse(
                result.id(),
                result.authorId(),
                result.title(),
                result.content(),
                result.viewCount());
    }

    public static List<PostResponse> toResponses(List<PostResult> results) {
        return results.stream().map(PostMapper::toResponse).toList();
    }
}
