package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

/**
 * @author penghuiping
 * @date 2021/3/3 10:58
 */
class RedisSetHandlers {

    static final Pair<String, RedisCmdHandler> SET_ADD = Pair.of(RedisCmd.SET_ADD, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_REMOVE = Pair.of(RedisCmd.SET_REMOVE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_MEMBERS = Pair.of(RedisCmd.SET_MEMBERS, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_IS_MEMBER = Pair.of(RedisCmd.SET_IS_MEMBER, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_POP = Pair.of(RedisCmd.SET_POP, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_UNION = Pair.of(RedisCmd.SET_UNION, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_INTER = Pair.of(RedisCmd.SET_INTER, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_DIFF = Pair.of(RedisCmd.SET_DIFF, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_SIZE = Pair.of(RedisCmd.SET_SIZE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SET_GET_RANDOM_MEMBER = Pair.of(RedisCmd.SET_GET_RANDOM_MEMBER, (redisManager, request, response) -> {

    });
}
