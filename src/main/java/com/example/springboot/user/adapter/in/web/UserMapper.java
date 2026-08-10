package com.example.springboot.user.adapter.in.web;

import com.example.springboot.user.adapter.in.web.dto.LoginRequest;
import com.example.springboot.user.adapter.in.web.dto.LoginResponse;
import com.example.springboot.user.adapter.in.web.dto.SignupRequest;
import com.example.springboot.user.adapter.in.web.dto.UserResponse;
import com.example.springboot.user.application.port.in.dto.LoginCommand;
import com.example.springboot.user.application.port.in.dto.LoginResult;
import com.example.springboot.user.application.port.in.dto.SignupCommand;
import com.example.springboot.user.application.port.in.dto.UserResult;

public final class UserMapper {

    private UserMapper() {}

    public static SignupCommand toCommand(SignupRequest request) {
        return new SignupCommand(request.email(), request.password());
    }

    public static LoginCommand toCommand(LoginRequest request) {
        return new LoginCommand(request.email(), request.password());
    }

    public static UserResponse toResponse(UserResult result) {
        return new UserResponse(result.id(), result.email());
    }

    public static LoginResponse toResponse(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.expiresAt());
    }
}
