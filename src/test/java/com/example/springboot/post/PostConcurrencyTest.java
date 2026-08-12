package com.example.springboot.post;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springboot.post.application.port.out.PostCommandPort;
import com.example.springboot.post.application.service.PostCommandService;
import com.example.springboot.post.domain.Post;
import com.example.springboot.support.containers.MySqlTestContainerConfiguration;
import com.example.springboot.user.application.port.out.UserCommandPort;
import com.example.springboot.user.application.port.out.UserQueryPort;
import com.example.springboot.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(
        properties = {
            "spring.datasource.hikari.maximum-pool-size=110",
            "spring.datasource.hikari.connection-timeout=10000"
        })
@Import(MySqlTestContainerConfiguration.class)
class PostConcurrencyTest {

    private static final int THREAD_COUNT = 100;

    @Autowired private PostCommandPort postCommandPort;

    @Autowired private PostCommandService postCommandService;

    @Autowired private UserCommandPort userCommandPort;

    @Autowired private UserQueryPort userQueryPort;

    private UUID postId;

    @BeforeEach
    void setUp() {
        User author =
                userQueryPort
                        .findByEmail("concurrency-test@example.com")
                        .orElseGet(
                                () ->
                                        userCommandPort.save(
                                                User.register(
                                                        "concurrency-test@example.com",
                                                        "password123",
                                                        "encoded-password")));

        Post saved = postCommandPort.save(Post.create(author.getId(), "동시성 테스트", "테스트 내용"));

        postId = saved.getId();
    }

    @Test
    @DisplayName("동시에 100번 조회해도 조회수 증가가 모두 반영된다")
    void concurrentViewsAreAllReflected() throws Exception {

        runConcurrent(() -> postCommandService.get(postId));

        long viewCount = postCommandPort.findById(postId).orElseThrow().getViewCount();

        assertThat(viewCount).isEqualTo(THREAD_COUNT);
    }

    private void runConcurrent(Runnable task) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        CountDownLatch ready = new CountDownLatch(THREAD_COUNT);

        CountDownLatch start = new CountDownLatch(1);

        CountDownLatch done = new CountDownLatch(THREAD_COUNT);

        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                futures.add(
                        executor.submit(
                                () -> {
                                    try {
                                        ready.countDown();
                                        start.await();
                                        task.run();
                                    } finally {
                                        done.countDown();
                                    }

                                    return null;
                                }));
            }

            if (!ready.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("작업 스레드 준비 시간이 초과되었습니다.");
            }

            start.countDown();

            if (!done.await(30, TimeUnit.SECONDS)) {
                throw new IllegalStateException("동시성 테스트 시간이 초과되었습니다.");
            }

            for (Future<?> future : futures) {
                future.get();
            }

        } finally {
            executor.shutdownNow();
        }
    }
}
