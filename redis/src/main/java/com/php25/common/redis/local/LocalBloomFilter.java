package com.php25.common.redis.local;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.php25.common.core.mess.StringBloomFilter;
import com.php25.common.redis.RBloomFilter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/2/24 16:59
 */
public class LocalBloomFilter implements RBloomFilter {

    private final StringBloomFilter bloomFilter;

    private final String key;

    private final LocalRedisManager redisManager;

    private final long expectedInsertions;

    private final double fpp;

    /**
     * @param expectedInsertions 预计插入量
     * @param fpp                可接受错误率
     */
    public LocalBloomFilter(String key, long expectedInsertions, double fpp, LocalRedisManager redisManager) {
        this.key = key;
        this.redisManager = redisManager;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = getInternalBloomFilter();
    }

    private StringBloomFilter getInternalBloomFilter() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.BLOOM_FILTER_GET, Lists.newArrayList(this.key, expectedInsertions, fpp));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (StringBloomFilter) res.get();
        }
        return null;
    }

    @Override
    public boolean mightContain(String key) {
        return this.bloomFilter.mightContain(key);
    }

    @Override
    public void put(String key) {
        this.bloomFilter.put(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalBloomFilter that = (LocalBloomFilter) o;
        return Objects.equal(bloomFilter, that.bloomFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(bloomFilter);
    }
}
