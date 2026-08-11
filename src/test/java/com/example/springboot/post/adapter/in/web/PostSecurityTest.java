package com.example.springboot.post.adapter.in.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.common.security.JwtAuthenticationFilter;
import com.example.springboot.common.security.SecurityConfig;
import com.example.springboot.common.security.handler.CustomAccessDeniedHandler;
import com.example.springboot.common.security.handler.CustomAuthenticationEntryPoint;
import com.example.springboot.post.application.port.in.PostCommandUseCase;
import com.example.springboot.post.application.port.in.PostQueryUseCase;
import com.example.springboot.post.application.port.in.dto.PostResult;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PostController.class)
@Import({
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    CustomAuthenticationEntryPoint.class,
    CustomAccessDeniedHandler.class
})
@DisplayName("게시글 API 보안 경로")
class PostSecurityTest {

    private static final UUID POST_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456789");
    private static final UUID AUTHOR_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456790");

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PostCommandUseCase postCommandUseCase;

    @MockitoBean private PostQueryUseCase postQueryUseCase;

    @MockitoBean private TokenProviderPort tokenProviderPort;

    @Test
    @DisplayName("공개 경로의 게시글 목록은 인증 없이 조회할 수 있다")
    void 공개_경로의_게시글_목록은_인증_없이_조회할_수_있다() throws Exception {
        // Given
        given(postQueryUseCase.getAll()).willReturn(List.of());

        // When & Then
        mockMvc.perform(get("/open-api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("공개 경로의 게시글 단건은 인증 없이 조회할 수 있다")
    void 공개_경로의_게시글_단건은_인증_없이_조회할_수_있다() throws Exception {
        // Given
        given(postCommandUseCase.get(POST_ID))
                .willReturn(new PostResult(POST_ID, AUTHOR_ID, "제목", "본문", 3L));

        // When & Then
        mockMvc.perform(get("/open-api/v1/posts/{postId}", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(POST_ID.toString()));
    }

    @Test
    @DisplayName("기존 게시글 조회 경로는 인증 없이 접근할 수 없다")
    void 기존_게시글_조회_경로는_인증_없이_접근할_수_없다() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/posts")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 작성은 인증 없이 접근할 수 없다")
    void 게시글_작성은_인증_없이_접근할_수_없다() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/posts")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 수정은 인증 없이 접근할 수 없다")
    void 게시글_수정은_인증_없이_접근할_수_없다() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/posts/{postId}", POST_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게시글 삭제는 인증 없이 접근할 수 없다")
    void 게시글_삭제는_인증_없이_접근할_수_없다() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/posts/{postId}", POST_ID))
                .andExpect(status().isUnauthorized());
    }
}
