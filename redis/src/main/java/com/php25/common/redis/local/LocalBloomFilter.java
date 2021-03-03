package com.php25.common.redis.local;

import com.php25.common.core.mess.StringBloomFilter;
import com.php25.common.redis.RBloomFilter;

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
        ExpiredCache expiredCache = this.redisManager.cache.getValue(this.key);
        if (null == expiredCache) {
            expiredCache = new ExpiredCache(Constants.DEFAULT_EXPIRED_TIME, this.key, new StringBloomFilter((int) expectedInsertions, fpp));
        }
        return (StringBloomFilter) expiredCache.getValue();
    }

    @Override
    public boolean mightContain(String key) {
        return this.bloomFilter.mightContain(key);
    }

    @Override
    public void put(String key) {
        this.bloomFilter.put(key);
    }
}
