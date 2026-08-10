package com.example.springboot.post.adapter.out.persistence;

import com.example.springboot.post.domain.Post;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "posts")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpaEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "post_id", nullable = false, updatable = false)
    private UUID id;

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

    private PostJpaEntity(Post post) {
        this.id = post.getId();
        this.authorId = post.getAuthorId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCount = post.getViewCount();
    }

    static PostJpaEntity from(Post post) {
        return new PostJpaEntity(post);
    }

    Post toDomain() {
        return Post.restore(id, authorId, title, content, viewCount);
    }
}
