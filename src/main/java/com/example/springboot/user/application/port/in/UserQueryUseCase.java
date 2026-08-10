package com.example.springboot.user.application.port.in;

import com.example.springboot.user.application.port.in.dto.UserResult;

public interface UserQueryUseCase {

    UserResult getMe(Long userId);
}
