package com.php25.common.redis.remote;

import com.php25.common.CommonAutoConfigure;
import com.php25.common.redis.RBloomFilter;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.impl.RedisManagerImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

/**
 * @author penghuiping
 * @date 2021/3/3 21:05
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class RBloomFilterRemoteTest {

    private static final Logger log = LoggerFactory.getLogger(RBloomFilterRemoteTest.class);
    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine").withExposedPorts(6379);
    private RedisManager redisManager;

    @Before
    public void before() {
        String address = redis.getContainerIpAddress();
        Integer port = redis.getFirstMappedPort();
        //单机
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setDatabase(0);
        redisConfiguration.setHostName(address);
        redisConfiguration.setPort(port);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        this.redisManager = new RedisManagerImpl(new StringRedisTemplate(lettuceConnectionFactory));
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
