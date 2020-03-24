package com.php25.common.redis;

import com.php25.common.core.util.JsonUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author penghuiping
 * @date 2020/1/4 22:31
 */
public class RHashImpl<T> implements RHash<T> {

    private StringRedisTemplate stringRedisTemplate;

    private String hashKey;

    private Class<T> model;

    public RHashImpl(StringRedisTemplate stringRedisTemplate, String hashKey, Class<T> model) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashKey = hashKey;
        this.model = model;
    }

    public RHashImpl(StringRedisTemplate stringRedisTemplate, String hashKey) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashKey = hashKey;
    }

    @Override
    public Boolean put(String key, Object value) {
        this.stringRedisTemplate.opsForHash().put(this.hashKey, key, JsonUtil.toJson(value));
        return true;
    }

    @Override
    public T get(String key) {
        Object value = this.stringRedisTemplate.opsForHash().get(this.hashKey, key);
        if (null == value) {
            return null;
        } else {
            return JsonUtil.fromJson(value.toString(), model);
        }
    }


    @Override
    public Long incr(String key) {
        return this.stringRedisTemplate.opsForHash().increment(this.hashKey, key, 1);
    }

    @Override
    public void delete(String key) {
        this.stringRedisTemplate.opsForHash().delete(this.hashKey, key);
    }
}
