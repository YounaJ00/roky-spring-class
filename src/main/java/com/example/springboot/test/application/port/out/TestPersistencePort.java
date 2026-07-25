package com.example.springboot.test.application.port.out;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.TestNotFoundException;
import java.util.List;
import java.util.Optional;

public interface TestPersistencePort {

    /**
     * 도메인을 저장하고 저장소가 채운 값까지 반영된 도메인을 돌려줍니다.
     *
     * @param test 저장할 도메인, 식별자가 없으면 신규 등록으로 처리
     * @return 식별자와 감사 시각이 채워진 도메인
     */
    Test save(Test test);

    Optional<Test> findById(Long id);

    List<Test> findAll();

    void deleteById(Long id);

    /**
     * 식별자로 도메인을 조회하고, 없으면 조회 실패로 처리합니다.
     *
     * @param id 조회할 도메인의 식별자
     * @return 조회된 도메인
     * @throws TestNotFoundException 해당 식별자의 도메인이 없는 경우
     */
    default Test getOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new TestNotFoundException(id));
    }
}
