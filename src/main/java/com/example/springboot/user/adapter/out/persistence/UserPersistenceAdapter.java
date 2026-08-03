package com.example.springboot.user.adapter.out.persistence;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.port.out.UserRepositoryPort;
import com.example.springboot.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            UserJpaEntity saved =
                    repository.saveAndFlush(
                            UserJpaEntity.create(user.getEmail(), user.getEncodedPassword()));

            return toDomain(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
    }

    private User toDomain(UserJpaEntity entity) {
        return User.restore(entity.getId(), entity.getEmail(), entity.getPasswordHash());
    }
}
