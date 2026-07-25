package com.example.springboot.test.application.query;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.ListTestsUseCase;
import com.example.springboot.test.application.port.out.TestQueryPersistencePort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListTestsService implements ListTestsUseCase {

    private final TestQueryPersistencePort testQueryPersistencePort;

    @Override
    public List<Test> list() {
        return testQueryPersistencePort.findAll();
    }
}
