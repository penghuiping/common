package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.php25.common.redis.RRateLimiter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.RATE_LIMIT_GET, Lists.newArrayList(this.key, rate));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (RateLimiter) res.get();
        }
        return null;
    }


    @Override
    public Boolean isAllowed() {
        return rateLimiter.tryAcquire();
    }
}
