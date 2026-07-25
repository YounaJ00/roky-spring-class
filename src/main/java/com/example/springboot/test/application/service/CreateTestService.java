package com.example.springboot.test.application.service;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.CreateTestUseCase;
import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateTestService implements CreateTestUseCase {

    private final TestPersistencePort testPersistencePort;

    @Override
    public TestResult create(CreateTestCommand command) {
        Test test = Test.create(command.title(), command.content());
        return TestResult.from(testPersistencePort.save(test));
    }
}
