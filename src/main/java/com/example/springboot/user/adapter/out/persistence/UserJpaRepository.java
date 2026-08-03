package com.example.springboot.user.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);
}
