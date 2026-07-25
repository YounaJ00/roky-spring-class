package com.example.springboot.test.application.port.in;

import com.example.springboot.test.Test;

public interface GetTestUseCase {

    Test get(Long id);
}
