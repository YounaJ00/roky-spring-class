package com.example.springboot.test.application.port.in;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;

public interface UpdateTestUseCase {

    Test update(Long id, UpdateTestCommand command);
}
