package com.php25.common.redis.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.redis.RString;
import org.apache.commons.lang3.NotImplementedException;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 模仿redis的api的本地string实现
 *
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalString implements RString {

    private final LocalRedisManager redisManager;

    public LocalString(LocalRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public <T> T get(String key, Class<T> cls) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_GET, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            ExpiredCache expiredCache = (ExpiredCache) res.get();
            return JsonUtil.fromJson(expiredCache.getValue().toString(), cls);
        }
        return null;
    }

    @Override
    public <T> T get(String key, TypeReference<T> cls) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_GET, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            ExpiredCache expiredCache = (ExpiredCache) res.get();
            return JsonUtil.fromJson(expiredCache.getValue().toString(), cls);
        }
        return null;
    }

    @Override
    public Boolean set(String key, Object value) {
        return this.set(key, value, Constants.DEFAULT_EXPIRED_TIME);
    }

    @Override
    public Boolean set(String key, Object value, Long expireTime) {
        Long expiredTime = Instant.now().toEpochMilli() + TimeUnit.SECONDS.toMillis(expireTime);
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_SET, Lists.newArrayList(key, value, expiredTime));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        return res.isPresent();
    }

    @Override
    public Boolean setNx(String key, Object value) {
        return this.setNx(key, value, Constants.DEFAULT_EXPIRED_TIME);
    }

    @Override
    public Boolean setNx(String key, Object value, Long expireTime) {
        Long expiredTime = Instant.now().toEpochMilli() + TimeUnit.SECONDS.toMillis(expireTime);
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_SET_NX, Lists.newArrayList(key, value, expiredTime));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        return res.isPresent();
    }

    @Override
    public Long incr(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_INCR, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            ExpiredCache expiredCache = (ExpiredCache) res.get();
            return JsonUtil.fromJson(expiredCache.getValue().toString(), Long.class);
        }
        return null;

    }

    @Override
    public Long decr(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_DECR, Lists.newArrayList(key));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            ExpiredCache expiredCache = (ExpiredCache) res.get();
            return JsonUtil.fromJson(expiredCache.getValue().toString(), Long.class);
        }
        return null;
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_SET_BIT, Lists.newArrayList(key, offset, value));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        }
        return false;
    }

    @Override
    public Boolean getBit(String key, long offset) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.STRING_GET_BIT, Lists.newArrayList(key, offset));
        CmdResponse cmdResponse = new CmdResponse();
        redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        }
        return false;
    }

    @Override
    public Long incrWithInitialValueAndSetExpiration(String key, Long value, Long expiration) {
        throw new NotImplementedException();
    }
}
