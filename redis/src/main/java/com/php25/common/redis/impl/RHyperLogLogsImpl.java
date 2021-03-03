package com.php25.common.redis.impl;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RHyperLogLogs;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author penghuiping
 * @date 2020/1/9 10:23
 */
public class RHyperLogLogsImpl implements RHyperLogLogs {

    private final StringRedisTemplate redisTemplate;

    private final String key;


    public RHyperLogLogsImpl(StringRedisTemplate redisTemplate, String key) {
        this.redisTemplate = redisTemplate;
        this.key = key;
    }

    @Override
    public void add(Object... values) {
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = JsonUtil.toJson(values[i]);
        }
        redisTemplate.opsForHyperLogLog().add(key, result);
    }

    @Override
    public Long size() {
        return redisTemplate.opsForHyperLogLog().size(key);
    }
}
