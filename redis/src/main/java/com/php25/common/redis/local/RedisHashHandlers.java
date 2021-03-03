package com.php25.common.redis.local;

import org.springframework.data.util.Pair;

/**
 * @author penghuiping
 * @date 2021/3/3 11:07
 */
public class RedisHashHandlers {

    static final Pair<String, RedisCmdHandler> HASH_PUT = Pair.of(RedisCmd.HASH_PUT, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> HASH_GET = Pair.of(RedisCmd.HASH_GET, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> HASH_DELETE = Pair.of(RedisCmd.HASH_DELETE, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> HASH_INCR = Pair.of(RedisCmd.HASH_INCR, (redisManager, request, response) -> {

    });

    static final Pair<String, RedisCmdHandler> HASH_DECR = Pair.of(RedisCmd.HASH_DECR, (redisManager, request, response) -> {

    });
}
