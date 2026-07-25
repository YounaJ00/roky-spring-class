package com.example.springboot.test.application.command;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.CreateTestUseCase;
import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import com.example.springboot.test.application.port.out.TestCommandPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateTestService implements CreateTestUseCase {

    private final TestCommandPersistencePort testCommandPersistencePort;

    @Override
    public Test create(CreateTestCommand command) {
        return testCommandPersistencePort.save(command.toDomain());
    }
}
