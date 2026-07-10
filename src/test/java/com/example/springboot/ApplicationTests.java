package com.example.springboot;

import com.example.springboot.support.containers.MySqlTestContainerConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(MySqlTestContainerConfiguration.class)
class ApplicationTests {

    @Test
    void contextLoads() {}
}
