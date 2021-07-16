package com.php25.common.redis.local;

/**
 * @author penghuiping
 * @date 2021/3/2 21:57
 */
interface RedisCmdHandler {

    /**
     * redis命令处理
     *
     * @param redisManager redis操作统一入口类
     * @param request      处理请求
     * @param response     处理结果
     */
    void handle(LocalRedisManager redisManager, CmdRequest request, CmdResponse response);
}
