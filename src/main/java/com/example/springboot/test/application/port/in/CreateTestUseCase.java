package com.example.springboot.test.application.port.in;

import com.example.springboot.test.Test;
import com.example.springboot.test.application.port.in.dto.CreateTestCommand;

public interface CreateTestUseCase {

    Test create(CreateTestCommand command);
}
