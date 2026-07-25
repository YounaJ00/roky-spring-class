package com.example.springboot.test.application.query;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import com.example.springboot.test.application.port.in.GetTestUseCase;
import com.example.springboot.test.application.port.out.TestQueryPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetTestService implements GetTestUseCase {

    private final TestQueryPersistencePort testQueryPersistencePort;

    @Override
    public Test get(Long id) {
        return testQueryPersistencePort
                .findById(id)
                .orElseThrow(() -> new TestNotFoundException(id));
    }
}
