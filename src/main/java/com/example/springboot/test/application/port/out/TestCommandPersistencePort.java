package com.example.springboot.test.application.port.out;

import com.example.springboot.test.Test;

public interface TestCommandPersistencePort {

    Test save(Test test);

    void delete(Test test);
}
