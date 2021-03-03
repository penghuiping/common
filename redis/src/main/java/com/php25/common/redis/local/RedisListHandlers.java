package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

/**
 * @author penghuiping
 * @date 2021/3/3 11:06
 */
public class RedisListHandlers {

    static final Pair<String, RedisCmdHandler> LIST_RIGHT_PUSH = Pair.of(RedisCmd.LIST_RIGHT_PUSH, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_PUSH = Pair.of(RedisCmd.LIST_LEFT_PUSH, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_RIGHT_POP = Pair.of(RedisCmd.LIST_RIGHT_POP, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_POP = Pair.of(RedisCmd.LIST_LEFT_POP, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_RANGE = Pair.of(RedisCmd.LIST_LEFT_RANGE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_LEFT_TRIM = Pair.of(RedisCmd.LIST_LEFT_TRIM, (redisManager, request, response) -> {

    });


    static final Pair<String, RedisCmdHandler> LIST_SIZE = Pair.of(RedisCmd.LIST_SIZE, (redisManager, request, response) -> {

    });


    static final Pair<String, RedisCmdHandler> LIST_BLOCK_LEFT_POP = Pair.of(RedisCmd.LIST_BLOCK_LEFT_POP, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> LIST_BLOCK_RIGHT_POP = Pair.of(RedisCmd.LIST_BLOCK_RIGHT_POP, (redisManager, request, response) -> {

    });
}
