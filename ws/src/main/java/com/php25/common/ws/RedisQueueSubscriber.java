package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.RedisManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private RedisManager redisService;

    private String serverId;

    private InnerMsgRetryQueue innerMsgRetryQueue;

    private ExecutorService singleThreadExecutor;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    public RedisQueueSubscriber(RedisManager redisService, String serverId,InnerMsgRetryQueue innerMsgRetryQueue) {
        this.redisService = redisService;
        this.serverId = serverId;
        this.innerMsgRetryQueue = innerMsgRetryQueue;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        registerRedisQueue();
        this.run();
    }


    @Override
    public void destroy() throws Exception {
        isRunning.compareAndSet(true,false);
        this.singleThreadExecutor.shutdown();
        unRegisterRedisQueue();
    }

    public void run() {
        this.singleThreadExecutor = Executors.newFixedThreadPool(1,r -> {
            Thread thread = new Thread(r);
            thread.setName("cpicwx-healthy-redis-queue-subscriber");
            return thread;
        });

        this.singleThreadExecutor.execute(() -> {
            RedisManagerImpl redisSpringBootService = (RedisManagerImpl) redisService;
            while (isRunning.get()) {
                try {
                    BoundListOperations<String, String> boundListOperations = redisSpringBootService.getRedisTemplate().boundListOps(Constants.prefix + this.serverId);
                    String msg = boundListOperations.rightPop(1,TimeUnit.SECONDS);
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
        RedisManagerImpl redisSpringBootService = (RedisManagerImpl) redisService;
        StringRedisTemplate stringRedisTemplate = redisSpringBootService.getRedisTemplate();
        BoundListOperations<String, String> boundListOperations = stringRedisTemplate.boundListOps(Constants.prefix + serverId);
        boundListOperations.expire(2, TimeUnit.HOURS);
    }

    private void unRegisterRedisQueue() {
        log.info("unregister redis Queue...;serverId:{}", serverId);
        RedisManagerImpl redisSpringBootService = (RedisManagerImpl) redisService;
        StringRedisTemplate stringRedisTemplate = redisSpringBootService.getRedisTemplate();
        stringRedisTemplate.delete(Constants.prefix + serverId);
    }

    /**
     * 心跳用于定时设置队列过期时间
     */
    @Scheduled(cron = "0 0 * * * ? ")
    public void redisQueueHeartBeat() {
        log.info("每小时重置:{}队列", serverId);
        redisService.expire(Constants.prefix + serverId, 2L, TimeUnit.HOURS);
    }


}
