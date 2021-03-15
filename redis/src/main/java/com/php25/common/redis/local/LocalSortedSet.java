package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.php25.common.redis.RSortedSet;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/2/24 17:00
 */
public class LocalSortedSet<T> implements RSortedSet<T> {

    private final String setKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    public LocalSortedSet(String setKey, Class<T> cls, LocalRedisManager redisManager) {
        this.setKey = setKey;
        this.cls = cls;
        this.redisManager = redisManager;
    }

    @Override
    public Boolean add(T element, double score) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_ADD, Lists.newArrayList(this.setKey, score, element));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public Long size() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_SIZE, Lists.newArrayList(this.setKey));
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
    public Set<T> range(long start, long end) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_RANGE, Lists.newArrayList(this.setKey, this.cls, start, end));
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
    public Set<T> reverseRange(long start, long end) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_REVERSE_RANGE, Lists.newArrayList(this.setKey, this.cls, start, end));
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
    public Set<T> rangeByScore(double min, double max) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_RANGE_BY_SCORE, Lists.newArrayList(this.setKey, this.cls, min, max));
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
    public Set<T> reverseRangeByScore(double min, double max) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_REVERSE_RANGE_BY_SCORE, Lists.newArrayList(this.setKey, this.cls, min, max));
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
    public Long rank(T element) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_RANK, Lists.newArrayList(this.setKey, this.cls, element));
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
    public Long reverseRank(T element) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_REVERSE_RANK, Lists.newArrayList(this.setKey, this.cls, element));
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
    public Long removeRangeByScore(double min, double max) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.SORTED_SET_REMOVE_RANGE_BY_SCORE, Lists.newArrayList(this.setKey, min, max));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return Long.parseLong(res.get().toString());
        } else {
            return null;
        }
    }
}
