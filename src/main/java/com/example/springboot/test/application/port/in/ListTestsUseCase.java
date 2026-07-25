package com.example.springboot.test.application.port.in;

import com.example.springboot.test.application.port.in.dto.TestResult;
import java.util.List;

public interface ListTestsUseCase {

    List<TestResult> list();
}
