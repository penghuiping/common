package com.php25.common.core.mess;

import com.google.common.hash.BloomFilter;

import java.nio.charset.StandardCharsets;

/**
 * 布隆过滤器
 *
 * @author penghuiping
 * @date 2019/9/3 17:27
 */
public class StringBloomFilter {

    private final BloomFilter<String> filter;

    /**
     * @param expectedInsertions 期待放入的元素数量
     * @param fpp                误报包含某个元素的概率(falsePositiveProbability )
     */
    public StringBloomFilter(int expectedInsertions, double fpp) {
        this.filter = BloomFilter.create((str, into) -> into.putString(str, StandardCharsets.UTF_8), expectedInsertions, fpp);
    }

    public boolean put(String value) {
        return this.filter.put(value);
    }

    public boolean mightContain(String value) {
        return this.filter.mightContain(value);
    }
}
