package com.example.springboot.user.application.port.out;

import com.example.springboot.user.domain.User;
import java.util.Optional;

public interface UserRepositoryPort {

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);
}
