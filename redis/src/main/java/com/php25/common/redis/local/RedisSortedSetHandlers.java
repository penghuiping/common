package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

/**
 * @author penghuiping
 * @date 2021/3/14 22:52
 */
public class RedisSortedSetHandlers {

    static final Pair<String, RedisCmdHandler> SORTED_SET_ADD = Pair.of(RedisCmd.SORTED_SET_ADD, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_SIZE = Pair.of(RedisCmd.SORTED_SET_SIZE, (redisManager, request, response) -> {

    });


    static final Pair<String, RedisCmdHandler> SORTED_SET_RANGE = Pair.of(RedisCmd.SORTED_SET_RANGE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANGE = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANGE, (redisManager, request, response) -> {

    });


    static final Pair<String, RedisCmdHandler> SORTED_SET_RANK = Pair.of(RedisCmd.SORTED_SET_RANK, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANK = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANK, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_RANGE_BY_SCORE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REVERSE_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_REVERSE_RANGE_BY_SCORE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> SORTED_SET_REMOVE_RANGE_BY_SCORE = Pair.of(RedisCmd.SORTED_SET_REMOVE_RANGE_BY_SCORE, (redisManager, request, response) -> {

    });
}
