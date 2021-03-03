package com.php25.common.redis.local;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RedisManager;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author penghuiping
 * @date 2021/3/2 17:38
 */

@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RBloomFilterLocalTest {
    private RedisManager redisManager;

    @Before
    public void before() {
        redisManager = new LocalRedisManager(1024);
    }

    @Test
    public void test() throws Exception {
        RBloomFilter bloomFilter = redisManager.bloomFilter("test_bl", 1000, 0.001);
        bloomFilter.put("jack");
        bloomFilter.put("mary");
        bloomFilter.put("ted");
        bloomFilter.put("tom");

        Assertions.assertThat(bloomFilter.mightContain("jack")).isTrue();
        Assertions.assertThat(bloomFilter.mightContain("jack1")).isFalse();
        Assertions.assertThat(bloomFilter.mightContain("alice")).isFalse();

        RBloomFilter bloomFilter1 = redisManager.bloomFilter("test_bl", 1000, 0.001);
        Assertions.assertThat(bloomFilter1.mightContain("jack")).isTrue();
        Assertions.assertThat(bloomFilter1.mightContain("jack1")).isFalse();
        Assertions.assertThat(bloomFilter1.mightContain("alice")).isFalse();
    }
}
