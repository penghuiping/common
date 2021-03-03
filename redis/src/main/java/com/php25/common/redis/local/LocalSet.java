package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.php25.common.redis.RSet;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalSet<T> implements RSet<T> {

    private final String setKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;


    public LocalSet(String setKey, Class<T> cls, LocalRedisManager redisManager) {
        this.setKey = setKey;
        this.redisManager = redisManager;
        this.cls = cls;
    }


    @Override
    public void add(T element) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_ADD, Lists.newArrayList(this.setKey, element));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public void remove(T element) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_REMOVE, Lists.newArrayList(this.setKey, element));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public Set<T> members() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_MEMBERS, Lists.newArrayList(this.setKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Set<T>) res.get();
        } else {
            return null;
        }
    }

    @Override
    public Boolean isMember(T element) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_IS_MEMBER, Lists.newArrayList(this.setKey, element));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Boolean) res.get();
        }
        return false;
    }

    @Override
    public T pop() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_POP, Lists.newArrayList(this.setKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (T) res.get();
        }
        return null;
    }

    @Override
    public Set<T> union(String otherSetKey) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_UNION, Lists.newArrayList(this.setKey, otherSetKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Set<T>) res.get();
        } else {
            return null;
        }
    }

    @Override
    public Set<T> inter(String otherSetKey) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_INTER, Lists.newArrayList(this.setKey, otherSetKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Set<T>) res.get();
        } else {
            return null;
        }
    }

    @Override
    public Set<T> diff(String otherSetKey) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_DIFF, Lists.newArrayList(this.setKey, otherSetKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (Set<T>) res.get();
        } else {
            return null;
        }
    }

    @Override
    public Long size() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_SIZE, Lists.newArrayList(this.setKey));
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
    public T getRandomMember() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SET_GET_RANDOM_MEMBER, Lists.newArrayList(this.setKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (T) res.get();
        }
        return null;
    }
}
