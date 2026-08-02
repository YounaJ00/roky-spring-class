package com.example.springboot.user.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_users_email",
                columnNames = "email"
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private UserJpaEntity(
            String email,
            String passwordHash
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static UserJpaEntity create(
            String email,
            String passwordHash
    ) {
        return new UserJpaEntity(
                email,
                passwordHash
        );
    }
}