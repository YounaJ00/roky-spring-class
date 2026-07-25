package com.example.springboot.test.application.port.in;

import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import com.example.springboot.test.application.port.in.dto.TestResult;

public interface CreateTestUseCase {

    TestResult create(CreateTestCommand command);
}
