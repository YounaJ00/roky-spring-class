package com.example.springboot.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.springboot.post.domain.exception.PostAuthorMismatchException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Post")
class PostTest {

    private static final UUID AUTHOR_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456790");
    private static final UUID OTHER_USER_ID =
            UUID.fromString("0198a123-4567-789a-8bcd-ef0123456791");

    @Test
    @DisplayName("작성자는 게시글을 수정할 수 있다")
    void 작성자는_게시글을_수정할_수_있다() {
        // Given
        Post post = Post.create(AUTHOR_ID, "기존 제목", "기존 본문");

        // When
        post.update(AUTHOR_ID, "수정 제목", "수정 본문");

        // Then
        assertThat(post.getTitle()).isEqualTo("수정 제목");
        assertThat(post.getContent()).isEqualTo("수정 본문");
    }

    @Test
    @DisplayName("작성자가 아니면 게시글을 수정할 수 없다")
    void 작성자가_아니면_게시글을_수정할_수_없다() {
        // Given
        Post post = Post.create(AUTHOR_ID, "기존 제목", "기존 본문");

        // When & Then
        assertThatThrownBy(() -> post.update(OTHER_USER_ID, "수정 제목", "수정 본문"))
                .isInstanceOf(PostAuthorMismatchException.class);
        assertThat(post.getTitle()).isEqualTo("기존 제목");
        assertThat(post.getContent()).isEqualTo("기존 본문");
    }

    @Test
    @DisplayName("작성자는 게시글 권한 검증을 통과한다")
    void 작성자는_게시글_권한_검증을_통과한다() {
        // Given
        Post post = Post.create(AUTHOR_ID, "제목", "본문");

        // When & Then
        assertThatCode(() -> post.validateAuthor(AUTHOR_ID)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("작성자가 아니면 게시글 권한 검증에 실패한다")
    void 작성자가_아니면_게시글_권한_검증에_실패한다() {
        // Given
        Post post = Post.create(AUTHOR_ID, "제목", "본문");

        // When & Then
        assertThatThrownBy(() -> post.validateAuthor(OTHER_USER_ID))
                .isInstanceOf(PostAuthorMismatchException.class);
    }
}
