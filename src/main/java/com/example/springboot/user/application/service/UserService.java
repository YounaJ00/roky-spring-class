package com.example.springboot.user.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.port.in.UserUseCase;
import com.example.springboot.user.application.port.out.AuthSecurityPort;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import com.example.springboot.user.application.port.out.UserRepositoryPort;
import com.example.springboot.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final AuthSecurityPort authSecurityPort;
    private final TokenProviderPort tokenProviderPort;

    @Override
    @Transactional
    public UserResult signup(SignupCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = authSecurityPort.encode(command.password());

        User user = User.register(command.email(), command.password(), encodedPassword);

        User saved = userRepositoryPort.save(user);

        return new UserResult(saved.getId(), saved.getEmail());
    }

    @Override
    public LoginResult login(LoginCommand command) {
        AuthSecurityPort.AuthenticatedUser authenticated =
                authSecurityPort.authenticate(command.email(), command.password());

        TokenProviderPort.IssuedToken token =
                tokenProviderPort.issue(authenticated.userId(), authenticated.email());

        return new LoginResult(token.value(), token.expiresAt());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult getMe(Long userId) {
        User user =
                userRepositoryPort
                        .findById(userId)
                        .orElseThrow(
                                () -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return new UserResult(user.getId(), user.getEmail());
    }
}
