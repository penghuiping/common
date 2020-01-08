package com.php25.common.redis;

import com.google.common.collect.Sets;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/1/8 09:49
 */
public class RSetImpl<T> implements RSet<T> {

    private StringRedisTemplate redisTemplate;

    private String setKey;

    private Class<T> model;

    public RSetImpl(StringRedisTemplate redisTemplate, String setKey, Class<T> model) {
        this.redisTemplate = redisTemplate;
        this.setKey = setKey;
        this.model = model;
    }

    @Override
    public void add(T element) {
        redisTemplate.opsForSet().add(setKey, JsonUtil.toJson(element));
    }

    @Override
    public Set<T> members() {
        Set<String> result = redisTemplate.opsForSet().members(setKey);
        if (null != result && !result.isEmpty()) {
            return result.stream().map(s -> JsonUtil.fromJson(s, model)).collect(Collectors.toSet());
        } else {
            return Sets.newHashSet();
        }
    }

    @Override
    public Boolean isMember(T element) {
        return redisTemplate.opsForSet().isMember(setKey, JsonUtil.toJson(element));
    }

    @Override
    public T pop() {
        String result = redisTemplate.opsForSet().pop(setKey);
        if (StringUtil.isBlank(result)) {
            return null;
        } else {
            return JsonUtil.fromJson(result, model);
        }
    }
}
