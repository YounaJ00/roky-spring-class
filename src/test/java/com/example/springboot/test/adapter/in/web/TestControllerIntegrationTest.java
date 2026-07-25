package com.example.springboot.test.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot.support.containers.MySqlTestContainerConfiguration;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MySqlTestContainerConfiguration.class)
@DisplayName("Test CRUD API")
class TestControllerIntegrationTest {

    private static final long MISSING_ID = 987654321L;

    @Autowired MockMvcTester mockMvc;
    @Autowired ObjectMapper objectMapper;

    private MvcTestResult create(String title, String content) {
        return mockMvc.post()
                .uri("/api/tests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(title, content))
                .exchange();
    }

    private MvcTestResult update(long id, String title, String content) {
        return mockMvc.put()
                .uri("/api/tests/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(title, content))
                .exchange();
    }

    private static String requestBody(String title, String content) {
        return """
                {"title":%s,"content":%s}"""
                .formatted(quote(title), quote(content));
    }

    private static String quote(String value) {
        return value == null ? "null" : "\"%s\"".formatted(value);
    }

    private JsonNode json(MvcTestResult result) {
        try {
            return objectMapper.readTree(result.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("응답 본문을 읽을 수 없습니다", e);
        }
    }

    private long createAndGetId(String title, String content) {
        return json(create(title, content)).get("id").asLong();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("생성에 성공하면 201과 저장된 리소스를 돌려준다")
    void 생성에_성공하면_저장된_리소스를_반환한다() {
        // When
        MvcTestResult result = create("스프링 수업", "헥사고날 정리");

        // Then
        assertThat(result).hasStatus(HttpStatus.CREATED);
        JsonNode body = json(result);
        assertThat(body.get("id").asLong()).isPositive();
        assertThat(body.get("title").asString()).isEqualTo("스프링 수업");
        assertThat(body.get("content").asString()).isEqualTo("헥사고날 정리");
        assertThat(body.get("createdAt").asString()).as("생성 시각은 저장 시점에 채워진다").isNotBlank();
    }

    @ParameterizedTest(name = "title=[{0}] content=[{1}]")
    @CsvSource(
            nullValues = "NULL",
            value = {"'  ',본문", "'',본문", "NULL,본문", "제목,'  '", "제목,NULL"})
    @DisplayName("제목이나 본문이 비어 있으면 400으로 거부한다")
    void 입력값이_비어있으면_생성을_거부한다(String title, String content) {
        // When
        MvcTestResult result = create(title, content);

        // Then
        assertThat(result).as("도메인 검증 실패가 500으로 새면 안 된다").hasStatus(HttpStatus.BAD_REQUEST);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("수정 응답의 수정 시각은 저장된 값과 같고 생성 시각보다 뒤선다")
    void 수정_응답의_수정_시각은_저장된_값과_일치한다() throws Exception {
        // Given
        JsonNode created = json(create("원래 제목", "원래 본문"));
        long id = created.get("id").asLong();
        Instant createdAt = Instant.parse(created.get("createdAt").asString());
        Thread.sleep(20);

        // When
        MvcTestResult updated = update(id, "새 제목", "새 본문");

        // Then
        assertThat(updated).hasStatus(HttpStatus.OK);
        Instant respondedUpdatedAt = Instant.parse(json(updated).get("updatedAt").asString());
        assertThat(respondedUpdatedAt).as("flush 이전 값을 응답하면 생성 시각에 머무른다").isAfter(createdAt);

        JsonNode refetched = json(mockMvc.get().uri("/api/tests/" + id).exchange());
        Instant storedUpdatedAt = Instant.parse(refetched.get("updatedAt").asString());
        assertThat(respondedUpdatedAt)
                .as("응답한 수정 시각과 저장된 수정 시각이 달라선 안 된다")
                .isEqualTo(storedUpdatedAt);
        assertThat(refetched.get("title").asString()).isEqualTo("새 제목");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("수정 요청의 값이 비어 있으면 400으로 거부한다")
    void 수정할_값이_비어있으면_수정을_거부한다() {
        // Given
        long id = createAndGetId("제목", "본문");

        // When
        MvcTestResult result = update(id, "  ", "새 본문");

        // Then
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("삭제하면 204를 반환하고 이후 조회는 404가 된다")
    void 삭제한_리소스는_다시_조회되지_않는다() {
        // Given
        long id = createAndGetId("지울 제목", "지울 본문");

        // When
        MvcTestResult deleted = mockMvc.delete().uri("/api/tests/" + id).exchange();

        // Then
        assertThat(deleted).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mockMvc.get().uri("/api/tests/" + id).exchange())
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("없는 식별자에 대한 조회·수정·삭제는 모두 404를 반환한다")
    void 없는_식별자는_모든_경로에서_404를_반환한다() {
        assertThat(mockMvc.get().uri("/api/tests/" + MISSING_ID).exchange())
                .hasStatus(HttpStatus.NOT_FOUND);
        assertThat(update(MISSING_ID, "제목", "본문")).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(mockMvc.delete().uri("/api/tests/" + MISSING_ID).exchange())
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("조회 실패 응답에는 원인을 알 수 있는 본문이 담긴다")
    void 조회_실패_응답은_원인을_알려준다() {
        // When
        MvcTestResult result = mockMvc.get().uri("/api/tests/" + MISSING_ID).exchange();

        // Then
        assertThat(json(result).get("detail").asString())
                .as("본문이 비면 클라이언트가 실패 원인을 알 수 없다")
                .isEqualTo("Test not found: " + MISSING_ID);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("목록 조회는 저장된 리소스를 담아 200을 반환한다")
    void 목록_조회는_저장된_리소스를_담는다() {
        // Given
        long id = createAndGetId("목록 제목", "목록 본문");

        // When
        MvcTestResult result = mockMvc.get().uri("/api/tests").exchange();

        // Then
        assertThat(result).hasStatus(HttpStatus.OK);
        JsonNode body = json(result);
        assertThat(body.isArray()).isTrue();
        assertThat(body.valueStream().anyMatch(node -> node.get("id").asLong() == id))
                .as("방금 저장한 리소스가 목록에 포함된다")
                .isTrue();
    }
}
