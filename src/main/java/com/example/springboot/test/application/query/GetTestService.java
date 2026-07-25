package com.example.springboot.test.application.query;

import com.example.springboot.test.application.port.in.GetTestUseCase;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTestService implements GetTestUseCase {

    private final TestPersistencePort testPersistencePort;

    @Override
    public TestResult get(Long id) {
        return TestResult.from(testPersistencePort.getOrThrow(id));
    }
}
