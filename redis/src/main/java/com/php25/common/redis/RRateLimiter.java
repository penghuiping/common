package com.php25.common.redis;

/**
 * @author penghuiping
 * @date 2020/3/25 15:54
 */
public interface RRateLimiter {

    /**
     * 是否允许访问
     *
     * @return true:允许访问 false:不允许访问
     */
    Boolean isAllowed();
}
