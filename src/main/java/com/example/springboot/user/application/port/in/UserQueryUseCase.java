package com.example.springboot.user.application.port.in;

import com.example.springboot.user.application.port.in.dto.UserResult;
import java.util.UUID;

public interface UserQueryUseCase {

    UserResult getMe(UUID userId);
}
