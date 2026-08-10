package com.example.springboot.user.application.service;

import com.example.springboot.common.exception.ApiException;
import com.example.springboot.user.application.port.in.UserQueryUseCase;
import com.example.springboot.user.application.port.in.dto.UserResult;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService implements UserQueryUseCase {

    private final UserQueryPort userQueryPort;

    @Override
    public UserResult getMe(Long userId) {
        User user =
                userQueryPort
                        .findById(userId)
                        .orElseThrow(
                                () -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return new UserResult(user.getId(), user.getEmail());
    }
}
