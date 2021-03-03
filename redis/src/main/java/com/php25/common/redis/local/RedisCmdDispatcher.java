package com.php25.common.redis.local;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/2 21:53
 */
class RedisCmdDispatcher {
    private static final Logger log = LoggerFactory.getLogger(RedisCmdDispatcher.class);
    private final ExecutorService singleConsumeWorker;
    private final Map<String, RedisCmdHandler> handlerMap = new HashMap<>();
    private final BlockingQueue<RedisCmdPair> buffer = new LinkedBlockingQueue<>();
    private LocalRedisManager redisManager;

    public RedisCmdDispatcher() {
        this.singleConsumeWorker = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("redis-cmd-consume-worker-%d")
                        .build());
        this.consumeRedisCmd();
    }

    public void setRedisManager(LocalRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public void registerHandler(String key, RedisCmdHandler handler) {
        handlerMap.put(key, handler);
    }

    public void registerHandler0(Pair<String, RedisCmdHandler> pair) {
        this.registerHandler(pair.getFirst(), pair.getSecond());
    }

    public void dispatch(CmdRequest cmdRequest, CmdResponse cmdResponse) {
        RedisCmdPair pair = new RedisCmdPair(cmdRequest, cmdResponse);
        buffer.offer(pair);
    }

    public void consumeRedisCmd() {
        this.singleConsumeWorker.submit(() -> {
            while (true) {
                try {
                    RedisCmdPair redisCmdPair = buffer.poll(1, TimeUnit.SECONDS);
                    if (null != redisCmdPair) {
                        RedisCmdHandler redisCmdHandler = handlerMap.get(redisCmdPair.getRequest().getCmd());
                        redisCmdHandler.handle(this.redisManager, redisCmdPair.getRequest(), redisCmdPair.getResponse());
                    }
                } catch (Exception e) {
                    log.error("消费local redis命令时出错!", e);
                }
            }
        });

    }
}
