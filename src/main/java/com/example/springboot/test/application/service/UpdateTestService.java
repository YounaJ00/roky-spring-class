package com.example.springboot.test.application.service;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.UpdateTestUseCase;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import com.example.springboot.test.application.port.out.TestPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateTestService implements UpdateTestUseCase {

    private final TestPersistencePort testPersistencePort;

    /**
     * 제목과 본문을 새 값으로 전체 교체합니다.
     *
     * <p>주의: 낙관적 락을 쓰지 않으므로 같은 리소스를 동시에 수정하면 나중 요청이 앞선 요청을 덮어쓴다. 부분 수정이 아닌 전체 교체(PUT) 의미이므로 의도한
     * 동작이며, 충돌 감지가 필요해지면 {@code @Version} 컬럼 마이그레이션과 409 변환을 함께 도입해야 한다.
     *
     * @param id 수정할 대상의 식별자
     * @param command 교체할 제목과 본문
     * @return 저장 후 감사 시각까지 반영된 결과
     * @throws com.example.springboot.test.application.TestNotFoundException 대상이 없는 경우
     */
    @Override
    public TestResult update(Long id, UpdateTestCommand command) {
        Test test = testPersistencePort.getOrThrow(id);
        test.update(command.title(), command.content());
        return TestResult.from(testPersistencePort.save(test));
    }
}
