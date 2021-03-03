package com.php25.common.redis.local;

/**
 * @author penghuiping
 * @date 2021/3/2 21:57
 */
interface RedisCmdHandler {

    void handle(LocalRedisManager redisManager, CmdRequest request, CmdResponse response);
}
