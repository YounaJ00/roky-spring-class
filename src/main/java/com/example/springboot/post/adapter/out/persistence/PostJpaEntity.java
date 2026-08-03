package com.example.springboot.post.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
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

    private PostJpaEntity(Long id, Long authorId, String title, String content, long viewCount) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
    }

    public static PostJpaEntity of(
            Long id, Long authorId, String title, String content, long viewCount) {
        return new PostJpaEntity(id, authorId, title, content, viewCount);
    }
}
