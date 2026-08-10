package com.example.springboot.post.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot.post.domain.Post;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PostJpaEntity 매핑")
class PostJpaEntityMappingTest {

    private static final UUID POST_ID = UUID.fromString("0198a123-4567-789a-8bcd-ef0123456789");

    @Test
    @DisplayName("도메인에서 엔티티로 갔다가 돌아와도 모든 필드가 보존된다")
    void 도메인과_엔티티_사이를_왕복해도_값이_보존된다() {
        // Given
        Post origin = Post.restore(POST_ID, 7L, "제목", "본문", 3L);

        // When
        Post restored = PostJpaEntity.from(origin).toDomain();

        // Then
        assertThat(restored.getId()).isEqualTo(POST_ID);
        assertThat(restored.getAuthorId()).isEqualTo(7L);
        assertThat(restored.getTitle()).isEqualTo("제목");
        assertThat(restored.getContent()).isEqualTo("본문");
        assertThat(restored.getViewCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("저장 전 도메인은 식별자가 없고 조회수가 0인 상태로 매핑된다")
    void 저장_전_도메인도_매핑할_수_있다() {
        // Given
        Post created = Post.create(7L, "제목", "본문");

        // When
        Post restored = PostJpaEntity.from(created).toDomain();

        // Then
        assertThat(restored.getId()).isNull();
        assertThat(restored.getAuthorId()).isEqualTo(7L);
        assertThat(restored.getTitle()).isEqualTo("제목");
        assertThat(restored.getContent()).isEqualTo("본문");
        assertThat(restored.getViewCount()).isZero();
    }
}
