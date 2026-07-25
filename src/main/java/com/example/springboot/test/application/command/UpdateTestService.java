package com.example.springboot.test.application.command;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.UpdateTestUseCase;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateTestService implements UpdateTestUseCase {

    private final TestPersistencePort testPersistencePort;

    @Override
    public TestResult update(Long id, UpdateTestCommand command) {
        Test test = testPersistencePort.getOrThrow(id);
        test.update(command.title(), command.content());
        return TestResult.from(testPersistencePort.save(test));
    }
}
