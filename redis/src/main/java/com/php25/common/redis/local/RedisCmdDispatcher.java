package com.php25.common.redis.local;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author penghuiping
 * @date 2021/3/2 21:53
 */
class RedisCmdDispatcher {
    private static final Logger log = LoggerFactory.getLogger(RedisCmdDispatcher.class);
    private final ExecutorService singleExpireCacheWorker;
    private final Map<String, RedisCmdHandler> handlerMap = new HashMap<>(128);
    private final Disruptor<RedisCmdPair> disruptor;
    private LocalRedisManager redisManager;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    /**
     * 5分钟清除一次过期缓存
     */
    private static final long CLEAN_EXPIRED_KEY_INTERVAL = 60000L;

    public RedisCmdDispatcher() {
        WaitStrategy strategy = new BlockingWaitStrategy();
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("redis-cmd-consume-worker-%d").build();
        this.disruptor =
                new Disruptor<>(RedisCmdPair::new, 1024, threadFactory, ProducerType.MULTI, strategy);
        disruptor.handleEventsWith((redisCmdPair, sequence, endOfBatch) -> {
            RedisCmdHandler redisCmdHandler = handlerMap.get(redisCmdPair.getRequest().getCmd());
            redisCmdHandler.handle(this.redisManager, redisCmdPair.getRequest(), redisCmdPair.getResponse());
        });
        disruptor.start();

        this.singleExpireCacheWorker = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(128),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat("redis-expire-cache-worker-%d")
                        .build());
        this.init();
    }

    public void stop() {
        disruptor.shutdown();
        //定时清理过期对象线程，直接关闭不影响
        this.singleExpireCacheWorker.shutdown();
        try {
            this.singleExpireCacheWorker.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void setRedisManager(LocalRedisManager redisManager) {
        this.redisManager = redisManager;
        this.singleExpireCacheWorker.submit(() -> {
            while (this.isRunning.get()) {
                try {
                    redisManager.cleanAllExpiredKeys();
                    Thread.sleep(CLEAN_EXPIRED_KEY_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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
        RingBuffer<RedisCmdPair> ringBuffer = disruptor.getRingBuffer();
        // 获取下一个可用位置的下标
        long sequence = ringBuffer.next();
        try {
            // 返回可用位置的元素
            RedisCmdPair redisCmdPair = ringBuffer.get(sequence);
            // 设置该位置元素的值
            redisCmdPair.setRequest(cmdRequest);
            redisCmdPair.setResponse(cmdResponse);
        } finally {
            ringBuffer.publish(sequence);
        }
    }


}
