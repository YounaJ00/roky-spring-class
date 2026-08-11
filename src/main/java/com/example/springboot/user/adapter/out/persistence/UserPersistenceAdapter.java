package com.example.springboot.user.adapter.out.persistence;

import com.example.springboot.user.application.exception.DuplicateEmailException;
import com.example.springboot.user.application.port.out.UserCommandPort;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserCommandPort, UserQueryPort {

    private static final String EMAIL_UNIQUE_CONSTRAINT = "uk_users_email";

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
            if (isEmailUniqueConstraintViolation(exception)) {
                throw new DuplicateEmailException(exception);
            }
            throw exception;
        }
    }

    private boolean isEmailUniqueConstraintViolation(DataIntegrityViolationException exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation) {
                return EMAIL_UNIQUE_CONSTRAINT.equals(constraintViolation.getConstraintName());
            }
            cause = cause.getCause();
        }
        return false;
    }
}
