package com.php25.common.core.mess;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;

import java.nio.charset.StandardCharsets;

/**
 * @author: penghuiping
 * @date: 2019/9/3 17:27
 * @description:
 */
public class StringBloomFilter {

    private BloomFilter<String> filter;

    /**
     * @param expectedInsertions 期待放入的元素数量
     * @param fpp                误报包含某个元素的概率(falsePositiveProbability )
     */
    public StringBloomFilter(int expectedInsertions, double fpp) {
        Funnel<String> strFunnel = (Funnel<String>) (str, into) -> into.putString(str, StandardCharsets.UTF_8);
        this.filter = BloomFilter.create(strFunnel, expectedInsertions, fpp);
    }

    public boolean put(String value) {
        return this.filter.put(value);
    }

    public boolean mightContain(String value) {
        return this.filter.mightContain(value);
    }
}
