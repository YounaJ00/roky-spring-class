package com.example.springboot.user.adapter.in.web;

import com.example.springboot.common.security.CustomUserPrincipal;
import com.example.springboot.user.application.port.in.UserUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    public UserUseCase.UserResult signup(@Valid @RequestBody SignupRequest request) {
        return userUseCase.signup(
                new UserUseCase.SignupCommand(request.email(), request.password()));
    }

    @PostMapping("/open-api/v1/auth/login")
    public UserUseCase.LoginResult login(@Valid @RequestBody LoginRequest request) {
        return userUseCase.login(new UserUseCase.LoginCommand(request.email(), request.password()));
    }

    @GetMapping("/api/v1/users/me")
    public UserUseCase.UserResult getMe(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return userUseCase.getMe(principal.getUserId());
    }

    public record SignupRequest(@Email String email, @Size(min = 8) String password) {}

    public record LoginRequest(@Email String email, @NotBlank String password) {}
}
