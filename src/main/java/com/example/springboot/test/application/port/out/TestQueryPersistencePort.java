package com.example.springboot.test.application.port.out;

import com.example.springboot.test.Test;
import java.util.List;
import java.util.Optional;

public interface TestQueryPersistencePort {

    Optional<Test> findById(Long id);

    List<Test> findAll();
}
