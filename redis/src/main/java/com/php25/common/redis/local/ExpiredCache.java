package com.php25.common.redis.local;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/2/25 13:43
 */
class ExpiredCache implements Delayed {
    /**
     * 到期时间戳
     */
    private Long expiredTime;

    /**
     * 缓存key
     */
    private String key;

    /**
     * 缓存值
     * 1. 对于String数据结构,需要先序列化成json字符串,在存入value
     * 2. 对于Lock,BloomFilter,RateLimiter,Hash,List,Set,SortedSet则直接存入value
     */
    private Object value;


    public ExpiredCache(Long expiredTime, String key, Object value) {
        this.expiredTime = expiredTime;
        this.key = key;
        this.value = value;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isExpired() {
        return Instant.now().toEpochMilli() - expiredTime >= 0;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return (this.expiredTime - System.currentTimeMillis()) * 1000000;
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return (int) (this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
    }
}
