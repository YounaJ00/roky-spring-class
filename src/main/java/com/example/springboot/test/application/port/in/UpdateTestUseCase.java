package com.example.springboot.test.application.port.in;

import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;

public interface UpdateTestUseCase {

    TestResult update(Long id, UpdateTestCommand command);
}
