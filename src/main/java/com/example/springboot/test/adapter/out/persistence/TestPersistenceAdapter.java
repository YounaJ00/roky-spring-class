package com.example.springboot.test.adapter.out.persistence;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.out.TestCommandPersistencePort;
import com.example.springboot.test.application.port.out.TestQueryPersistencePort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TestPersistenceAdapter implements TestCommandPersistencePort, TestQueryPersistencePort {

    private final SpringDataTestRepository testRepository;

    @Override
    public Test save(Test test) {
        return testRepository.save(TestJpaEntity.from(test)).toDomain();
    }

    @Override
    public Optional<Test> findById(Long id) {
        return testRepository.findById(id).map(TestJpaEntity::toDomain);
    }

    @Override
    public List<Test> findAll() {
        return testRepository.findAll().stream().map(TestJpaEntity::toDomain).toList();
    }

    @Override
    public void delete(Test test) {
        testRepository.delete(TestJpaEntity.from(test));
    }
}
