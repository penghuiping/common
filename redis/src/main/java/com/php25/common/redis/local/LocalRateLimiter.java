package com.php25.common.redis.local;

import com.google.common.util.concurrent.RateLimiter;
import com.php25.common.redis.RRateLimiter;

/**
 * @author penghuiping
 * @date 2021/2/24 16:59
 */
public class LocalRateLimiter implements RRateLimiter {
    private final RateLimiter rateLimiter;

    private final LocalRedisManager redisManager;

    private final String key;

    private final int rate;

    public LocalRateLimiter(String key, int rate, LocalRedisManager redisManager) {
        this.key = key;
        this.redisManager = redisManager;
        this.rate = rate;
        this.rateLimiter = getInternalRateLimiter();
    }

    private RateLimiter getInternalRateLimiter() {
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, this.key, RateLimiter.create(this.rate));
        }
        return (RateLimiter) expiredCache.getValue();

    }


    @Override
    public Boolean isAllowed() {
        return rateLimiter.tryAcquire();
    }
}
