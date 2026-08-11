package com.example.springboot.user.adapter.out.persistence;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.port.out.UserCommandPort;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserCommandPort, UserQueryPort {

    private final UserJpaRepository repository;

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            UserJpaEntity saved =
                    repository.saveAndFlush(
                            UserJpaEntity.create(user.getEmail(), user.getEncodedPassword()));

            return saved.toDomain();
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
    }
}
