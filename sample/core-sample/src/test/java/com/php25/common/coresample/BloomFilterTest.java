package com.php25.common.coresample;

import com.php25.common.core.mess.StringBloomFilter;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * @author: penghuiping
 * @date: 2019/9/3 16:53
 * @description:
 */
public class BloomFilterTest {

    @Test
    public void test() {
        StringBloomFilter stringBloomFilter = new StringBloomFilter(100, 0.01);
        stringBloomFilter.put("123");
        stringBloomFilter.put("abc");
        stringBloomFilter.put("ddd");
        stringBloomFilter.put("18812345678");
        stringBloomFilter.put("ddd1");
        stringBloomFilter.put("ddd2");
        stringBloomFilter.put("ddd3");

        Assertions.assertThat(stringBloomFilter.mightContain("abc")).isTrue();
        Assertions.assertThat(stringBloomFilter.mightContain("ddd")).isTrue();
        Assertions.assertThat(stringBloomFilter.mightContain("123")).isTrue();
        Assertions.assertThat(stringBloomFilter.mightContain("18812345678")).isTrue();
        Assertions.assertThat(stringBloomFilter.mightContain("ddd5")).isFalse();
    }

}
