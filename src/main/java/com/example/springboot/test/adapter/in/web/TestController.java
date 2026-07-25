package com.example.springboot.test.adapter.in.web;

import com.example.springboot.test.adapter.in.web.dto.CreateTestRequest;
import com.example.springboot.test.adapter.in.web.dto.TestResponse;
import com.example.springboot.test.adapter.in.web.dto.UpdateTestRequest;
import com.example.springboot.test.application.port.in.CreateTestUseCase;
import com.example.springboot.test.application.port.in.DeleteTestUseCase;
import com.example.springboot.test.application.port.in.GetTestUseCase;
import com.example.springboot.test.application.port.in.ListTestsUseCase;
import com.example.springboot.test.application.port.in.UpdateTestUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final CreateTestUseCase createTestUseCase;
    private final GetTestUseCase getTestUseCase;
    private final ListTestsUseCase listTestsUseCase;
    private final UpdateTestUseCase updateTestUseCase;
    private final DeleteTestUseCase deleteTestUseCase;

    @PostMapping
    public ResponseEntity<TestResponse> create(@RequestBody CreateTestRequest request) {
        TestResponse response = TestResponse.from(createTestUseCase.create(request.toCommand()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public TestResponse get(@PathVariable Long id) {
        return TestResponse.from(getTestUseCase.get(id));
    }

    @GetMapping
    public List<TestResponse> list() {
        return listTestsUseCase.list().stream().map(TestResponse::from).toList();
    }

    @PutMapping("/{id}")
    public TestResponse update(@PathVariable Long id, @RequestBody UpdateTestRequest request) {
        return TestResponse.from(updateTestUseCase.update(id, request.toCommand()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteTestUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
