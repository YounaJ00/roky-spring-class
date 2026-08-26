package com.example.springboot.user.application.port.out;

import com.example.springboot.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserQueryPort {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);
}
