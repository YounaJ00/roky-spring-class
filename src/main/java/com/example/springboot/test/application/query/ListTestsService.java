package com.example.springboot.test.application.query;

import com.example.springboot.test.application.port.in.ListTestsUseCase;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListTestsService implements ListTestsUseCase {

    private final TestPersistencePort testPersistencePort;

    @Override
    public List<TestResult> list() {
        return testPersistencePort.findAll().stream().map(TestResult::from).toList();
    }
}
