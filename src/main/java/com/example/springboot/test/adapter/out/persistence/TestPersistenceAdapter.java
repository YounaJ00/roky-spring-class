package com.example.springboot.test.adapter.out.persistence;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class TestPersistenceAdapter implements TestPersistencePort {

    private final SpringDataTestRepository testRepository;

    /**
     * 저장 후 감사 시각까지 반영된 도메인을 돌려줍니다.
     *
     * <p>감사 시각을 채우는 {@code @PreUpdate} 콜백은 flush 시점에 동작하므로, flush 이전 상태를 매핑하면 수정 시각이 갱신 이전 값으로 남는다.
     */
    @Override
    public Test save(Test test) {
        return testRepository.saveAndFlush(TestJpaEntity.from(test)).toDomain();
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
    public void deleteById(Long id) {
        testRepository.deleteById(id);
    }
}
