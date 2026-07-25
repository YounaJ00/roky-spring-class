package com.example.springboot.test.application.service;

import com.example.springboot.test.application.port.in.DeleteTestUseCase;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteTestService implements DeleteTestUseCase {

    private final TestPersistencePort testPersistencePort;

    @Override
    public void delete(Long id) {
        testPersistencePort.getOrThrow(id);
        testPersistencePort.deleteById(id);
    }
}
