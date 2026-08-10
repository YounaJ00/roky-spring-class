package com.example.springboot.post.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PostJpaRepository extends JpaRepository<PostJpaEntity, UUID> {

    List<PostJpaEntity> findAllByOrderByCreatedAtDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    @Query(
            """
            select p
            from PostJpaEntity p
            where p.id = :postId
            """)
    Optional<PostJpaEntity> findByIdForUpdate(@Param("postId") UUID postId);
}
