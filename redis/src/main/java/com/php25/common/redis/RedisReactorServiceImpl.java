package com.php25.common.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.php25.common.core.util.JsonUtil;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author: penghuiping
 * @date: 2019/3/19 22:32
 * @description:
 */
public class RedisReactorServiceImpl implements RedisAsyncService {

    ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public RedisReactorServiceImpl(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Mono<Long> remove(String... keys) {
        return reactiveRedisTemplate.delete(keys);
    }

    @Override
    public Mono<Long> remove(String key) {
        return reactiveRedisTemplate.delete(key);
    }

    @Override
    public Mono<Boolean> exists(String key) {
        return reactiveRedisTemplate.hasKey(key);
    }

    @Override
    public <T> Mono<T> get(String key, Class<T> cls) {
        return reactiveRedisTemplate.opsForValue().get(key).map(s -> JsonUtil.fromJson(s, cls));
    }

    @Override
    public <T> Mono<T> get(String key, TypeReference<T> cls) {
        return reactiveRedisTemplate.opsForValue().get(key).map(s -> JsonUtil.fromJson(s, cls));
    }

    @Override
    public Mono<Boolean> setNx(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().setIfAbsent(key, JsonUtil.toJson(value));
    }

    @Override
    public Mono<Boolean> set(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().set(key, JsonUtil.toJson(value));
    }

    @Override
    public Mono<Boolean> set(String key, Object value, Long expireTime) {
        return reactiveRedisTemplate.opsForValue().set(key, JsonUtil.toJson(value), Duration.of(expireTime, ChronoUnit.SECONDS));
    }

    @Override
    public Mono<Long> remainTimeToLive(String key) {
        return reactiveRedisTemplate.getExpire(key).map(Duration::getSeconds);
    }

    @Override
    public Mono<Long> incr(String key) {
        return reactiveRedisTemplate.createMono(connection -> connection.numberCommands().incr(ByteBuffer.wrap(key.getBytes())));
    }

    @Override
    public Mono<Boolean> expire(String key, Long expireTime, TimeUnit timeUnit) {
        return reactiveRedisTemplate.expire(key, Duration.of(timeUnit.toMillis(expireTime), ChronoUnit.MILLIS));
    }

    @Override
    public Mono<Boolean> expireAt(String key, Date date) {
        return reactiveRedisTemplate.expireAt(key, Instant.ofEpochMilli(date.getTime()));
    }
}
