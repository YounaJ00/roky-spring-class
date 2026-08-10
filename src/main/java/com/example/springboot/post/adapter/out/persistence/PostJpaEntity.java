package com.example.springboot.post.adapter.out.persistence;

import com.example.springboot.post.domain.Post;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private PostJpaEntity(Long id, Long authorId, String title, String content, long viewCount) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    static PostJpaEntity from(Post post) {
        return new PostJpaEntity(
                post.getId(),
                post.getAuthorId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount());
    }

    Post toDomain() {
        return Post.restore(id, authorId, title, content, viewCount);
    }
}
