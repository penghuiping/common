package com.php25.common.ws;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.redis.RList;
import com.php25.common.redis.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
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
public class RedisQueueSubscriber implements InitializingBean, DisposableBean {

    private final RedisManager redisService;

    private final String serverId;

    private final InnerMsgRetryQueue innerMsgRetryQueue;

    private ExecutorService singleThreadExecutor;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public RedisQueueSubscriber(RedisManager redisService, String serverId, InnerMsgRetryQueue innerMsgRetryQueue) {
        this.redisService = redisService;
        this.serverId = serverId;
        this.innerMsgRetryQueue = innerMsgRetryQueue;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        registerRedisQueue();
        this.singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("ws-redis-queue-subscriber-%d")
                        .build());
        isRunning.compareAndSet(false, true);
        this.run();
    }


    @Override
    public void destroy() throws Exception {
        try {
            isRunning.compareAndSet(true, false);
            boolean res = this.singleThreadExecutor.awaitTermination(1, TimeUnit.SECONDS);
            if (res) {
                log.info("关闭ws:RedisQueueSubscriber成功");
            }
        } catch (Exception e) {
            log.error("关闭ws:RedisQueueSubscriber出错", e);
        }
        unRegisterRedisQueue();
    }

    public void run() {
        this.singleThreadExecutor.execute(() -> {
            while (isRunning.get()) {
                try {
                    RList<String> rList = redisService.list(Constants.prefix + this.serverId, String.class);
                    String msg = rList.blockRightPop(1, TimeUnit.SECONDS);
                    if (!StringUtil.isBlank(msg)) {
                        BaseRetryMsg baseRetry = JsonUtil.fromJson(msg, BaseRetryMsg.class);
                        innerMsgRetryQueue.put(baseRetry);
                    }
                } catch (Exception e) {
                    log.error("轮训获取redis队列中的消息出错", e);
                }
            }
        });
    }

    private void registerRedisQueue() {
        log.info("register redis Queue....;serverId:{}", serverId);
        redisService.expire(Constants.prefix + serverId, 2L, TimeUnit.HOURS);
    }

    private void unRegisterRedisQueue() {
        log.info("unregister redis Queue...;serverId:{}", serverId);
        redisService.remove(Constants.prefix + serverId);
    }

    /**
     * 心跳用于定时设置队列过期时间
     */
    @Scheduled(cron = "0 0 * * * ? ")
    public void redisQueueHeartBeat() {
        log.info("每小时重置:{}队列", serverId);
        redisService.expire(Constants.prefix + serverId, 2L, TimeUnit.HOURS);
    }

    /**
     * 每分钟打印一次统计信息
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void logStatus() {
        SpringContextHolder.getBean0(InnerMsgRetryQueue.class).stats();
        SpringContextHolder.getBean0(GlobalSession.class).stats();
    }

}
