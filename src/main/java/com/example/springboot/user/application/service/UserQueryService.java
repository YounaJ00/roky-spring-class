package com.example.springboot.user.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.exception.UserErrorCode;
import com.example.springboot.user.application.port.in.UserQueryUseCase;
import com.example.springboot.user.application.port.in.dto.UserResult;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService implements UserQueryUseCase {

    private final UserQueryPort userQueryPort;

    @Override
    public UserResult getMe(UUID userId) {
        User user =
                userQueryPort
                        .findById(userId)
                        .orElseThrow(() -> new ApiException(UserErrorCode.USER_NOT_FOUND));

        return new UserResult(user.getId(), user.getEmail());
    }
}
