package com.example.springboot.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 감사 시각 자동 기록 설정.
 *
 * <p>{@code @SpringBootApplication}이 아니라 별도 설정 클래스에 두어야 {@code @WebMvcTest} 같은 웹 슬라이스가 JPA 컨텍스트 없이도
 * 뜬다.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfiguration {

    /**
     * 감사 시각을 마이크로초까지만 기록합니다.
     *
     * <p>컬럼 타입이 {@code DATETIME(6)}이라 나노초는 저장 시 잘린다. 자르지 않으면 메모리에 남은 값과 조회한 값의 정밀도가 달라져 같은 필드가 요청
     * 경로마다 다르게 보인다.
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(Instant.now().truncatedTo(ChronoUnit.MICROS));
    }
}
