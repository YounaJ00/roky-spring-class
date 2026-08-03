package com.example.springboot.post.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository
        extends JpaRepository<PostJpaEntity, Long> {

    List<PostJpaEntity> findAllByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(
            @QueryHint(
                    name = "jakarta.persistence.lock.timeout",
                    value = "3000"
            )
    )
    @Query("""
            select p
            from PostJpaEntity p
            where p.id = :postId
            """)
    Optional<PostJpaEntity> findByIdForUpdate(
            @Param("postId") Long postId
    );
}