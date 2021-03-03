package com.php25.common.redis_local;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.local.LocalRedisManager;
import org.assertj.core.api.Assertions;
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

    @Test
    public void test() throws Exception {
        RedisManager redisManager = new LocalRedisManager(1024);
        RBloomFilter bloomFilter = redisManager.bloomFilter("test_bl", 100, 0.001);
        bloomFilter.put("jack");
        bloomFilter.put("mary");
        bloomFilter.put("ted");
        bloomFilter.put("tom");

        Assertions.assertThat(bloomFilter.mightContain("jack")).isTrue();
        Assertions.assertThat(bloomFilter.mightContain("alice")).isFalse();

        RBloomFilter bloomFilter1 = redisManager.bloomFilter("test_bl", 1000, 0.001);
        Assertions.assertThat(bloomFilter).isEqualTo(bloomFilter1);
    }
}
