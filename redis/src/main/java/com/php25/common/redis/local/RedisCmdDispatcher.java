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
    private final ExecutorService singleExpireCacheWorker;
    private final Map<String, RedisCmdHandler> handlerMap = new HashMap<>();
    private final BlockingQueue<RedisCmdPair> buffer = new LinkedBlockingQueue<>();
    private LocalRedisManager redisManager;

    public RedisCmdDispatcher() {
        this.singleConsumeWorker = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("redis-cmd-consume-worker-%d")
                        .build());
        this.singleExpireCacheWorker = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("redis-expire-cache-worker-%d")
                        .build());
        this.init();
    }

    public void setRedisManager(LocalRedisManager redisManager) {
        this.redisManager = redisManager;
        this.consumeRedisCmd();
        this.singleExpireCacheWorker.submit(() -> {
            while (true) {
                try {
                    redisManager.cleanAllExpiredKeys();
                    log.info("cache size:{}", redisManager.cache.size());
                    Thread.sleep(5 * 60000L);
                } catch (Exception e) {
                    log.error("redis缓存清理操作失败", e);
                }
            }
        });
    }

    private void init() {
        this.registerHandler0(RedisStringHandlers.STRING_GET);
        this.registerHandler0(RedisStringHandlers.STRING_SET);
        this.registerHandler0(RedisStringHandlers.STRING_SET_NX);
        this.registerHandler0(RedisStringHandlers.STRING_INCR);
        this.registerHandler0(RedisStringHandlers.STRING_DECR);
        this.registerHandler0(RedisStringHandlers.STRING_SET_BIT);
        this.registerHandler0(RedisStringHandlers.STRING_GET_BIT);

        this.registerHandler0(RedisStringHandlers.REMOVE);
        this.registerHandler0(RedisStringHandlers.CLEAN_ALL_EXPIRE);
        this.registerHandler0(RedisStringHandlers.EXISTS);
        this.registerHandler0(RedisStringHandlers.GET_EXPIRE);
        this.registerHandler0(RedisStringHandlers.EXPIRE);
        this.registerHandler0(RedisStringHandlers.BLOOM_FILTER_GET);
        this.registerHandler0(RedisStringHandlers.RATE_LIMIT_GET);

        this.registerHandler0(RedisSetHandlers.SET_ADD);
        this.registerHandler0(RedisSetHandlers.SET_REMOVE);
        this.registerHandler0(RedisSetHandlers.SET_MEMBERS);
        this.registerHandler0(RedisSetHandlers.SET_IS_MEMBER);
        this.registerHandler0(RedisSetHandlers.SET_POP);
        this.registerHandler0(RedisSetHandlers.SET_UNION);
        this.registerHandler0(RedisSetHandlers.SET_INTER);
        this.registerHandler0(RedisSetHandlers.SET_DIFF);
        this.registerHandler0(RedisSetHandlers.SET_SIZE);
        this.registerHandler0(RedisSetHandlers.SET_GET_RANDOM_MEMBER);

        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_ADD);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_SIZE);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_RANGE);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_REVERSE_RANGE);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_RANK);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_REVERSE_RANK);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_RANGE_BY_SCORE);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_REVERSE_RANGE_BY_SCORE);
        this.registerHandler0(RedisSortedSetHandlers.SORTED_SET_REMOVE_RANGE_BY_SCORE);

        this.registerHandler0(RedisListHandlers.LIST_INIT);
        this.registerHandler0(RedisListHandlers.LIST_RIGHT_PUSH);
        this.registerHandler0(RedisListHandlers.LIST_LEFT_PUSH);
        this.registerHandler0(RedisListHandlers.LIST_RIGHT_POP);
        this.registerHandler0(RedisListHandlers.LIST_LEFT_POP);
        this.registerHandler0(RedisListHandlers.LIST_LEFT_RANGE);
        this.registerHandler0(RedisListHandlers.LIST_LEFT_TRIM);
        this.registerHandler0(RedisListHandlers.LIST_SIZE);

        this.registerHandler0(RedisHashHandlers.HASH_PUT);
        this.registerHandler0(RedisHashHandlers.HASH_PUT_NX);
        this.registerHandler0(RedisHashHandlers.HASH_HAS_KEY);
        this.registerHandler0(RedisHashHandlers.HASH_GET);
        this.registerHandler0(RedisHashHandlers.HASH_DELETE);
        this.registerHandler0(RedisHashHandlers.HASH_INCR);
        this.registerHandler0(RedisHashHandlers.HASH_DECR);
        this.registerHandler0(RedisHashHandlers.HASH_DECR);
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
