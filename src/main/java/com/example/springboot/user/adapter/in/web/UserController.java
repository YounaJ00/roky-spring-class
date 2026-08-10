package com.example.springboot.user.adapter.in.web;

import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.user.adapter.in.web.dto.LoginRequest;
import com.example.springboot.user.adapter.in.web.dto.LoginResponse;
import com.example.springboot.user.adapter.in.web.dto.SignupRequest;
import com.example.springboot.user.adapter.in.web.dto.UserResponse;
import com.example.springboot.user.application.port.in.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PostMapping("/open-api/v1/auth/signup")
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        return UserMapper.toResponse(userUseCase.signup(UserMapper.toCommand(request)));
    }

    @PostMapping("/open-api/v1/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return UserMapper.toResponse(userUseCase.login(UserMapper.toCommand(request)));
    }

    @GetMapping("/api/v1/users/me")
    public UserResponse getMe(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return UserMapper.toResponse(userUseCase.getMe(principal.getUserId()));
    }
}
