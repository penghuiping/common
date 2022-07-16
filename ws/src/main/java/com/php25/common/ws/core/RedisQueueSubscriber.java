package com.php25.common.ws.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import com.php25.common.ws.protocal.BaseMsg;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * redis队列消息订阅者,这里使用轮询方式从redis队列中pull消息
 *
 * @author penghuiping
 * @date 2020/08/10
 */
@Slf4j
public class RedisQueueSubscriber implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    private final RedisManager redisManager;

    private final String serverId;

    private ExecutorService singleThreadExecutor;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private final SessionContext sessionContext;

    public RedisQueueSubscriber(RedisManager redisManager, String serverId, SessionContext sessionContext) {
        this.redisManager = redisManager;
        this.serverId = serverId;
        this.sessionContext = sessionContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1024),
                new ThreadFactoryBuilder().setNameFormat("ws-redis-queue-subscriber-%d")
                        .build(), new ThreadPoolExecutor.AbortPolicy());
        this.run();
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent contextClosedEvent) {
        try {
            isRunning.compareAndSet(true, false);
            this.singleThreadExecutor.shutdown();
            boolean res = this.singleThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
            if (res) {
                log.info("关闭ws:RedisQueueSubscriber成功");
            }
        } catch (InterruptedException e) {
            log.error("关闭ws:RedisQueueSubscriber出错", e);
            Thread.currentThread().interrupt();
        }
        unRegisterRedisQueue();
    }

    public void run() {
        this.singleThreadExecutor.execute(() -> {
            while (isRunning.get()) {
                try {
                    RList<String> rList = redisManager.list(Constants.prefix + this.serverId, String.class);
                    String msg = rList.blockRightPop(1, TimeUnit.SECONDS);
                    if (!StringUtil.isBlank(msg)) {
                        BaseMsg baseMsg = JsonUtil.fromJson(msg, BaseMsg.class);
                        ExpirationSocketSession expirationSocketSession = this.sessionContext.getExpirationSocketSession(baseMsg.getSessionId());
                        expirationSocketSession.put(baseMsg);
                    }
                } catch (Exception e) {
                    log.error("轮训获取redis队列中的消息出错", e);
                }
            }
        });
    }

    private void unRegisterRedisQueue() {
        log.info("unregister redis Queue...;serverId:{}", serverId);
        redisManager.remove(Constants.prefix + serverId);
    }
}
