package com.php25.common.coresample;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.php25.common.core.service.StringBloomFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author: penghuiping
 * @date: 2019/9/3 16:53
 * @description:
 */
public class BloomFilterTest {


    @Test
    public void test1() {
        StringBloomFilter stringBloomFilter = new StringBloomFilter(100, 0.01);

        stringBloomFilter.put("123");
        stringBloomFilter.put("abc");
        stringBloomFilter.put("ddd");
        stringBloomFilter.put("18812345678");
        stringBloomFilter.put("ddd1");
        stringBloomFilter.put("ddd2");
        stringBloomFilter.put("ddd3");

        Assert.assertTrue(stringBloomFilter.mightContain("abc"));
        Assert.assertTrue(stringBloomFilter.mightContain("ddd"));
        Assert.assertTrue(stringBloomFilter.mightContain("123"));
        Assert.assertTrue(stringBloomFilter.mightContain("18812345678"));
    }


    @Test
    public void test2() {
        Funnel<String> strFunnel = new Funnel<String>() {
            @Override
            public void funnel(String str, PrimitiveSink into) {
                into.putString(str, Charsets.UTF_8);
            }
        };

        BloomFilter<String> filter = BloomFilter.create(strFunnel, 500, 0.01);

        filter.put("123");
        filter.put("abc");
        filter.put("ddd");
        filter.put("18812345678");
        filter.put("ddd1");
        filter.put("ddd2");
        filter.put("ddd3");

        Assert.assertTrue(filter.mightContain("abc"));
        Assert.assertTrue(filter.mightContain("ddd"));
        Assert.assertTrue(filter.mightContain("123"));
        Assert.assertTrue(filter.mightContain("18812345678"));
    }
}
