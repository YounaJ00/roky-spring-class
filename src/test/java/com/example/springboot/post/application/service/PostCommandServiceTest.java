package com.example.springboot.post.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.post.application.port.in.dto.PostCommand;
import com.example.springboot.post.application.port.out.PostCommandPort;
import com.example.springboot.post.domain.Post;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostCommandService")
class PostCommandServiceTest {

    private static final UUID POST_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456789");
    private static final UUID AUTHOR_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456790");
    private static final UUID OTHER_USER_ID =
            UUID.fromString("0198a123-4567-789a-8bcd-ef0123456791");

    @Mock private PostCommandPort postCommandPort;

    @InjectMocks private PostCommandService service;

    @Test
    @DisplayName("작성자가 아닌 사용자는 게시글을 수정할 수 없다")
    void 작성자가_아니면_게시글을_수정할_수_없다() {
        // Given
        Post post = Post.restore(POST_ID, AUTHOR_ID, "기존 제목", "기존 본문", 0L);
        PostCommand command = new PostCommand("수정 제목", "수정 본문");
        given(postCommandPort.findById(POST_ID)).willReturn(Optional.of(post));

        // When & Then
        assertForbidden(() -> service.update(OTHER_USER_ID, POST_ID, command));
        then(postCommandPort).should(never()).save(any(Post.class));
    }

    @Test
    @DisplayName("작성자가 아닌 사용자는 게시글을 삭제할 수 없다")
    void 작성자가_아니면_게시글을_삭제할_수_없다() {
        // Given
        Post post = Post.restore(POST_ID, AUTHOR_ID, "제목", "본문", 0L);
        given(postCommandPort.findById(POST_ID)).willReturn(Optional.of(post));

        // When & Then
        assertForbidden(() -> service.delete(OTHER_USER_ID, POST_ID));
        then(postCommandPort).should(never()).deleteById(any(UUID.class));
    }

    private void assertForbidden(Runnable operation) {
        assertThatThrownBy(operation::run)
                .isInstanceOfSatisfying(
                        ApiException.class,
                        exception -> {
                            assertThat(exception.getStatus()).isEqualTo(HttpStatus.FORBIDDEN);
                            assertThat(exception.getCode()).isEqualTo("POST_AUTHOR_REQUIRED");
                            assertThat(exception.getMessage()).isEqualTo("작성자만 변경할 수 있습니다.");
                        });
    }
}
