package cn.graht.user.boot;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * @author GRAHT
 */
@Slf4j
@Order(1)
@Component
public class RunOnlyOneMethod implements CommandLineRunner {

    @Resource
    private Redisson redisson;

    private final String INIT_REDISSON_LOCK_KEY = "fufu:user:init:data:lock:";
    private final Integer MAX_RETRIES = 10;
    private final long INITIAL_BACKOFF = 3000;

    @Override
    public void run(String... args) {
        RLock lock = null;
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                lock = redisson.getLock(INIT_REDISSON_LOCK_KEY);
                lock.lock();
                start();
                break;
            } catch (Exception e) {
                retryCount++;
                log.error("初始化失败: 准备重试 (尝试次数: {})", retryCount, e);
                if (retryCount >= MAX_RETRIES) {
                    log.error("达到最大重试次数，放弃重试", e);
                    throw new RuntimeException("初始化失败，达到最大重试次数", e);
                }
                try {
                    long backoff = INITIAL_BACKOFF * (1L << (retryCount - 1)); // 指数退避
                    Thread.sleep(backoff);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("重试期间线程被中断", ex);
                    throw new RuntimeException(ex);
                }
            } finally {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private void start() throws Exception {
        log.info("执行初始化开始");

        log.info("初始化完成");

    }
}
