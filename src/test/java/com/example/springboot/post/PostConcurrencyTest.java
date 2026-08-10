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
import java.util.concurrent.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(
        properties = {
            "spring.datasource.hikari.maximum-pool-size=110",
            "spring.datasource.hikari.connection-timeout=10000"
        })
@Import({MySqlTestContainerConfiguration.class, PostConcurrencyTest.TestConfig.class})
class PostConcurrencyTest {

    private static final int THREAD_COUNT = 100;

    @Autowired private PostCommandPort postCommandPort;

    @Autowired private PostCommandService postCommandService;

    @Autowired private UserCommandPort userCommandPort;

    @Autowired private UserQueryPort userQueryPort;

    @Autowired private LostUpdateWorker lostUpdateWorker;

    private Long postId;

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
    @DisplayName("락이 없으면 조회수 증가가 유실된다")
    void lostUpdateWithoutLock() throws Exception {
        CountDownLatch allRead = new CountDownLatch(THREAD_COUNT);

        runConcurrent(() -> lostUpdateWorker.increase(postId, allRead));

        long viewCount = postCommandPort.findById(postId).orElseThrow().getViewCount();

        System.out.println("[락 없음] 최종 조회수 = " + viewCount);

        assertThat(viewCount).isLessThan(THREAD_COUNT);
    }

    @Test
    @DisplayName("비관적 락 적용 후 조회수 100이 보장된다")
    void pessimisticLockPreventsLostUpdate() throws Exception {

        runConcurrent(() -> postCommandService.get(postId));

        long viewCount = postCommandPort.findById(postId).orElseThrow().getViewCount();

        System.out.println("[비관적 락] 최종 조회수 = " + viewCount);

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

    @TestConfiguration(proxyBeanMethods = false)
    static class TestConfig {

        @Bean
        LostUpdateWorker lostUpdateWorker(PostCommandPort postCommandPort) {
            return new LostUpdateWorker(postCommandPort);
        }
    }

    static class LostUpdateWorker {

        private final PostCommandPort postCommandPort;

        LostUpdateWorker(PostCommandPort postCommandPort) {
            this.postCommandPort = postCommandPort;
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void increase(Long postId, CountDownLatch allRead) {
            Post post = postCommandPort.findById(postId).orElseThrow();

            allRead.countDown();

            try {
                if (!allRead.await(20, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("동시 조회 대기 시간이 초과되었습니다.");
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(exception);
            }

            post.increaseViewCount();

            postCommandPort.save(post);
        }
    }
}
