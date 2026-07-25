package com.example.springboot.test.application.port.in;

import com.example.springboot.test.application.port.in.dto.TestResult;

public interface GetTestUseCase {

    TestResult get(Long id);
}
