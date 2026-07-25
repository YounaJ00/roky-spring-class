package com.example.springboot.test.application.command;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.in.DeleteTestUseCase;
import com.example.springboot.test.application.port.out.TestCommandPersistencePort;
import com.example.springboot.test.application.port.out.TestQueryPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteTestService implements DeleteTestUseCase {

    private final TestCommandPersistencePort testCommandPersistencePort;
    private final TestQueryPersistencePort testQueryPersistencePort;

    @Override
    public void delete(Long id) {
        testCommandPersistencePort.delete(findById(id));
    }

    private Test findById(Long id) {
        return testQueryPersistencePort
                .findById(id)
                .orElseThrow(() -> new TestNotFoundException(id));
    }
}
