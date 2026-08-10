package com.example.springboot.user.application.port.out;

import com.example.springboot.user.domain.User;

public interface UserCommandPort {

    boolean existsByEmail(String email);

    User save(User user);
}
