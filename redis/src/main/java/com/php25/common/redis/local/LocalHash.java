package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.php25.common.redis.RHash;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 模仿redis的api的本地hash实现
 *
 * @author penghuiping
 * @date 2021/2/24 16:58
 */
public class LocalHash<T> implements RHash<T> {

    private final String hashKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    public LocalHash(String hashKey, Class<T> cls, LocalRedisManager redisManager) {
        this.hashKey = hashKey;
        this.redisManager = redisManager;
        this.cls = cls;
    }


    @Override
    public Boolean put(String key, T value) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_PUT, Lists.newArrayList(this.hashKey, key, value));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        } else {
            return false;
        }
    }

    @Override
    public T get(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_GET, Lists.newArrayList(this.hashKey, key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (T) res.get();
        } else {
            return null;
        }
    }

    @Override
    public void delete(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_DELETE, Lists.newArrayList(this.hashKey, key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public Long incr(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_INCR, Lists.newArrayList(this.hashKey, key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return Long.parseLong(res.get().toString());
        } else {
            return null;
        }
    }

    @Override
    public Long decr(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_DECR, Lists.newArrayList(this.hashKey, key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return Long.parseLong(res.get().toString());
        } else {
            return null;
        }
    }

    @Override
    public Boolean putIfAbsent(String key, T value) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_PUT_NX, Lists.newArrayList(this.hashKey, key, value));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        } else {
            return false;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.HASH_HAS_KEY, Lists.newArrayList(this.hashKey, key));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        } else {
            return false;
        }
    }
}
