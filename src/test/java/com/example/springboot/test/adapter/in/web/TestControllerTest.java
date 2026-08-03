package com.example.springboot.test.adapter.in.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springboot.test.application.port.in.CreateTestUseCase;
import com.example.springboot.test.application.port.in.DeleteTestUseCase;
import com.example.springboot.test.application.port.in.GetTestUseCase;
import com.example.springboot.test.application.port.in.ListTestsUseCase;
import com.example.springboot.test.application.port.in.UpdateTestUseCase;
import com.example.springboot.test.application.port.in.dto.CreateTestCommand;
import com.example.springboot.test.application.port.in.dto.TestResult;
import com.example.springboot.test.application.port.in.dto.UpdateTestCommand;
import com.example.springboot.user.application.port.out.TokenProviderPort;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TestController")
class TestControllerTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-02-03T04:05:06Z");

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreateTestUseCase createTestUseCase;

    @MockitoBean private GetTestUseCase getTestUseCase;

    @MockitoBean private ListTestsUseCase listTestsUseCase;

    @MockitoBean private UpdateTestUseCase updateTestUseCase;

    @MockitoBean private DeleteTestUseCase deleteTestUseCase;

    @MockitoBean private TokenProviderPort tokenProviderPort;

    @org.junit.jupiter.api.Test
    @DisplayName("생성 요청은 201과 생성된 리소스를 반환한다")
    void 생성_요청은_201을_반환한다() throws Exception {
        // Given
        given(createTestUseCase.create(new CreateTestCommand("스프링 수업", "헥사고날 아키텍처 정리")))
                .willReturn(new TestResult(1L, "스프링 수업", "헥사고날 아키텍처 정리", CREATED_AT, CREATED_AT));

        // When & Then
        mockMvc.perform(
                        post("/api/tests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"title":"스프링 수업","content":"헥사고날 아키텍처 정리"}
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("스프링 수업"))
                .andExpect(jsonPath("$.content").value("헥사고날 아키텍처 정리"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T00:00:00Z"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("단건 조회는 200과 도메인의 각 필드를 정확히 매핑해 반환한다")
    void 단건_조회는_도메인_필드를_그대로_매핑한다() throws Exception {
        // Given
        given(getTestUseCase.get(1L))
                .willReturn(new TestResult(1L, "제목", "본문", CREATED_AT, UPDATED_AT));

        // When & Then
        mockMvc.perform(get("/api/tests/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("본문"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-01T00:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").value("2026-02-03T04:05:06Z"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("목록 조회는 200과 전체 항목을 반환한다")
    void 목록_조회는_전체_항목을_반환한다() throws Exception {
        // Given
        given(listTestsUseCase.list())
                .willReturn(
                        List.of(
                                new TestResult(1L, "첫 번째", "본문1", CREATED_AT, CREATED_AT),
                                new TestResult(2L, "두 번째", "본문2", CREATED_AT, CREATED_AT)));

        // When & Then
        mockMvc.perform(get("/api/tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("첫 번째"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("두 번째"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("저장된 항목이 없으면 200과 빈 배열을 반환한다")
    void 항목이_없으면_빈_배열을_반환한다() throws Exception {
        // Given
        given(listTestsUseCase.list()).willReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/tests"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("수정 요청은 200과 변경된 리소스를 반환하고 유스케이스에 식별자와 명령을 전달한다")
    void 수정_요청은_변경된_리소스를_반환한다() throws Exception {
        // Given
        given(updateTestUseCase.update(1L, new UpdateTestCommand("새 제목", "새 본문")))
                .willReturn(new TestResult(1L, "새 제목", "새 본문", CREATED_AT, UPDATED_AT));

        // When & Then
        mockMvc.perform(
                        put("/api/tests/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"title":"새 제목","content":"새 본문"}
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새 제목"))
                .andExpect(jsonPath("$.content").value("새 본문"))
                .andExpect(jsonPath("$.updatedAt").value("2026-02-03T04:05:06Z"));
    }

    @org.junit.jupiter.api.Test
    @DisplayName("삭제 요청은 204와 빈 본문을 반환한다")
    void 삭제_요청은_204를_반환한다() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/tests/{id}", 1L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        then(deleteTestUseCase).should().delete(1L);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("요청 값이 비어 있으면 유스케이스를 호출하지 않고 400을 반환한다")
    void 요청_값이_비어있으면_400을_반환한다() throws Exception {
        // When & Then
        mockMvc.perform(
                        post("/api/tests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        """
                                        {"title":"  ","content":"본문"}
                                        """))
                .andExpect(status().isBadRequest());
        then(createTestUseCase).shouldHaveNoInteractions();
    }
}
