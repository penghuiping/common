package com.php25.common.core.mess.ratelimiter;


import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 限流算法-令牌桶(非线程安全)
 *
 * @author penghuiping-001
 * @date 2022/7/20 14:00
 */
public class TokenBucketRateLimiter implements RateLimiter {
    /**
     * 每秒生产令牌的速率
     */
    private final BigDecimal rate;

    /**
     * 桶的大小，桶里最多放多少令牌
     */
    private final Long capacity;

    /**
     * 上一次访问时间
     */
    private long lastRefreshed;

    /**
     * 上一次访问时桶里令牌数量
     */
    private long lastTokenNumber;

    public TokenBucketRateLimiter(long windowLimit, long windowTimeSize, TimeUnit windowTimeSizeUnit) {
        this.rate = new BigDecimal(windowLimit / TimeUnit.SECONDS.convert(windowTimeSize, windowTimeSizeUnit));
        this.capacity = windowLimit;
        lastTokenNumber = this.capacity;
        lastRefreshed = System.nanoTime();
    }

    @Override
    public Boolean isAllowed() {
        long now = System.nanoTime();
        long delta = now - lastRefreshed;
        long deltaSecond = TimeUnit.SECONDS.convert(delta, TimeUnit.NANOSECONDS);
        long newTokenNumber = this.rate.multiply(BigDecimal.valueOf(deltaSecond)).longValue();
        long currentTokenNumber = Math.min(this.capacity, this.lastTokenNumber + newTokenNumber);
        if (currentTokenNumber >= 1) {
            this.lastTokenNumber = currentTokenNumber - 1;
            this.lastRefreshed = now;
            return true;
        } else {
            if (this.lastTokenNumber != currentTokenNumber) {
                this.lastTokenNumber = currentTokenNumber;
                this.lastRefreshed = now;
            }
            return false;
        }

    }
}
