package com.example.springboot.test.application.command;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.in.UpdateTestUseCase;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import com.example.springboot.test.application.port.out.TestCommandPersistencePort;
import com.example.springboot.test.application.port.out.TestQueryPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateTestService implements UpdateTestUseCase {

    private final TestCommandPersistencePort testCommandPersistencePort;
    private final TestQueryPersistencePort testQueryPersistencePort;

    @Override
    public Test update(Long id, UpdateTestCommand command) {
        Test test = findById(id);
        command.applyTo(test);
        return testCommandPersistencePort.save(test);
    }

    private Test findById(Long id) {
        return testQueryPersistencePort
                .findById(id)
                .orElseThrow(() -> new TestNotFoundException(id));
    }
}
