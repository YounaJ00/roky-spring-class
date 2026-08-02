package com.example.springboot.user.application.port.in;

import java.time.Instant;

public interface UserUseCase {
    UserResult signup(SignupCommand command);

    LoginResult login(LoginCommand command);

    UserResult getMe(Long userId);

    record SignupCommand(String email, String password){}

    record LoginCommand(String email, String password){}

    record UserResult(Long id, String email){}

    record LoginResult(String accessToken, Instant expiresAt){}
}

