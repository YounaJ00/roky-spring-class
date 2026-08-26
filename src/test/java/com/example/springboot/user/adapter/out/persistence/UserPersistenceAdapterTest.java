package com.example.springboot.user.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.springboot.user.application.exception.DuplicateEmailException;
import com.example.springboot.user.domain.User;
import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserPersistenceAdapter")
class UserPersistenceAdapterTest {

    private static final String EMAIL_UNIQUE_CONSTRAINT = "uk_users_email";

    @Mock private UserJpaRepository repository;

    @InjectMocks private UserPersistenceAdapter adapter;

    @Test
    @DisplayName("이메일 unique 제약 위반은 중복 이메일 예외로 번역한다")
    void 이메일_unique_제약_위반은_중복_이메일_예외로_번역한다() {
        // Given
        User user = User.register("duplicate@example.com", "password123", "encoded-password");
        DataIntegrityViolationException databaseException =
                integrityViolation(EMAIL_UNIQUE_CONSTRAINT);
        given(repository.saveAndFlush(any(UserJpaEntity.class))).willThrow(databaseException);

        // When & Then
        assertThatThrownBy(() -> adapter.save(user))
                .isInstanceOf(DuplicateEmailException.class)
                .hasCause(databaseException);
    }

    @Test
    @DisplayName("이메일 중복이 아닌 무결성 위반은 원래 예외를 유지한다")
    void 다른_무결성_위반은_원래_예외를_유지한다() {
        // Given
        User user = User.register("user@example.com", "password123", "encoded-password");
        DataIntegrityViolationException databaseException =
                integrityViolation("uk_users_other_constraint");
        given(repository.saveAndFlush(any(UserJpaEntity.class))).willThrow(databaseException);

        // When & Then
        assertThatThrownBy(() -> adapter.save(user)).isSameAs(databaseException);
    }

    private DataIntegrityViolationException integrityViolation(String constraintName) {
        ConstraintViolationException constraintViolation =
                new ConstraintViolationException(
                        "constraint violation", new SQLException(), constraintName);
        return new DataIntegrityViolationException("data integrity violation", constraintViolation);
    }
}
