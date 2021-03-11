package com.php25.common.redis.local;

import com.google.common.collect.Lists;
import com.php25.common.redis.RList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 模仿redis的api的本地list实现
 *
 * @author penghuiping
 * @date 2021/2/24 16:59
 */
public class LocalList<T> implements RList<T> {

    private static final Logger log = LoggerFactory.getLogger(LocalList.class);

    private final String listKey;

    private final LocalRedisManager redisManager;

    private final Class<T> cls;

    /**
     * 用于blockRightPop与blockLeftPop操作
     */
    private BlockingQueue<Boolean> pipe;

    public LocalList(String listKey, Class<T> cls, LocalRedisManager redisManager) {
        this.listKey = listKey;
        this.redisManager = redisManager;
        this.cls = cls;
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_INIT, Lists.newArrayList(this.listKey));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            LinkedListPlus<Object> list = (LinkedListPlus<Object>) res.get();
            this.pipe = list.getPipe();
        }
    }

    @Override
    public Long rightPush(T value) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_RIGHT_PUSH, Lists.newArrayList(this.listKey, value));
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
    public Long leftPush(T value) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_LEFT_PUSH, Lists.newArrayList(this.listKey, value));
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
    public T rightPop() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_RIGHT_POP, Lists.newArrayList(this.listKey));
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
    public T leftPop() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_LEFT_POP, Lists.newArrayList(this.listKey));
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
    public List<T> leftRange(long start, long end) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_LEFT_RANGE, Lists.newArrayList(this.listKey, start, end));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
        if (res.isPresent()) {
            return (List<T>) res.get();
        } else {
            return null;
        }
    }

    @Override
    public void leftTrim(long start, long end) {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_LEFT_TRIM, Lists.newArrayList(this.listKey, start, end));
        CmdResponse cmdResponse = new CmdResponse();
        this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
        cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
    }

    @Override
    public Long size() {
        CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_SIZE, Lists.newArrayList(this.listKey));
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
    public T blockLeftPop(long timeout, TimeUnit timeUnit) {
        long expiredTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        //轮训等待
        while (System.currentTimeMillis() < expiredTime) {
            CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_LEFT_POP, Lists.newArrayList(this.listKey));
            CmdResponse cmdResponse = new CmdResponse();
            this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
            Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
            if (res.isPresent()) {
                return (T) res.get();
            }
            //list这时候是空的,需要等待
            try {
                this.pipe.poll(expiredTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("RList blockLeftPop操作出错", e);
            }
        }
        return null;
    }


    @Override
    public T blockRightPop(long timeout, TimeUnit timeUnit) {
        //轮训等待
        long expiredTime = System.currentTimeMillis() + timeUnit.toMillis(timeout);
        while (System.currentTimeMillis() < expiredTime) {
            CmdRequest cmdRequest = new CmdRequest(RedisCmd.LIST_RIGHT_POP, Lists.newArrayList(this.listKey));
            CmdResponse cmdResponse = new CmdResponse();
            this.redisManager.redisCmdDispatcher.dispatch(cmdRequest, cmdResponse);
            Optional<Object> res = cmdResponse.getResult(Constants.TIME_OUT, TimeUnit.SECONDS);
            if (res.isPresent()) {
                return (T) res.get();
            }
            //list这时候是空的,需要等待
            try {
                this.pipe.poll(expiredTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("RList blockRightPop操作出错", e);
            }
        }
        return null;
    }
}
