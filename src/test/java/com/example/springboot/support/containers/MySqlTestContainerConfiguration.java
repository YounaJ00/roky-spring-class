package com.example.springboot.support.containers;

import java.time.Duration;
import java.util.Map;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.mysql.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MySqlTestContainerConfiguration {

    /**
     * 테스트용 MySQL 컨테이너.
     *
     * <p>데이터 디렉터리를 tmpfs에 두어 InnoDB 초기화를 메모리에서 처리한다. 테스트 DB는 컨테이너와 함께 버려지므로 디스크 내구성이 필요 없고, 디스크가 느린
     * 환경에서 기동 시간이 3분에서 20초 아래로 줄어 JDBC 연결 재시도 한도 안에 안정적으로 들어온다.
     */
    @Bean
    @ServiceConnection
    MySQLContainer mysqlContainer() {
        return new MySQLContainer("mysql:8.4")
                .withDatabaseName("spring_boot")
                .withUsername("spring_boot")
                .withPassword("spring_boot")
                .withTmpFs(Map.of("/var/lib/mysql", "rw"))
                .withStartupTimeout(Duration.ofMinutes(3));
    }
}
