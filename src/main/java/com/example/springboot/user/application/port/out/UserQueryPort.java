package com.example.springboot.user.application.port.out;

import com.example.springboot.user.domain.User;
import java.util.Optional;

public interface UserQueryPort {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);
}
