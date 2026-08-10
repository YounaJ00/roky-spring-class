package com.example.springboot.user.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.port.in.UserCommandUseCase;
import com.example.springboot.user.application.port.in.dto.LoginCommand;
import com.example.springboot.user.application.port.in.dto.LoginResult;
import com.example.springboot.user.application.port.in.dto.SignupCommand;
import com.example.springboot.user.application.port.in.dto.UserResult;
import com.example.springboot.user.application.port.out.AuthSecurityPort;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import com.example.springboot.user.application.port.out.UserCommandPort;
import com.example.springboot.user.application.port.out.dto.AuthenticatedUser;
import com.example.springboot.user.application.port.out.dto.IssuedToken;
import com.example.springboot.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService implements UserCommandUseCase {

    private final UserCommandPort userCommandPort;
    private final AuthSecurityPort authSecurityPort;
    private final TokenProviderPort tokenProviderPort;

    @Override
    @Transactional
    public UserResult signup(SignupCommand command) {
        if (userCommandPort.existsByEmail(command.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = authSecurityPort.encode(command.password());

        User user = User.register(command.email(), command.password(), encodedPassword);

        User saved = userCommandPort.save(user);

        return new UserResult(saved.getId(), saved.getEmail());
    }

    @Override
    public LoginResult login(LoginCommand command) {
        AuthenticatedUser authenticated =
                authSecurityPort.authenticate(command.email(), command.password());

        IssuedToken token = tokenProviderPort.issue(authenticated.userId(), authenticated.email());

        return new LoginResult(token.value(), token.expiresAt());
    }
}
