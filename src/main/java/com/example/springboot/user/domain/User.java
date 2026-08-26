package com.example.springboot.user.domain;

import java.util.UUID;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UUID id;
    private final String email;
    private final String encodedPassword;

    private User(UUID id, String email, String encodedPassword) {
        validateEmail(email);
        validateEncodedPassword(encodedPassword);

        this.id = id;
        this.email = email;
        this.encodedPassword = encodedPassword;
    }

    // register: 신규 회원 생성, id 없음
    public static User register(String email, String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        return new User(null, email, encodedPassword);
    }

    // 기존 회원 복원, id 있음
    public static User restore(UUID id, String email, String encodedPassword) {
        return new User(id, email, encodedPassword);
    }

    private static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("올바르지 않은 이메일입니다.");
        }
    }

    private static void validateEncodedPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("암호화된 비밀번호가 필요합니다.");
        }
    }
}
