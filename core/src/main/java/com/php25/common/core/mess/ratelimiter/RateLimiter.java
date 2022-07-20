package com.php25.common.core.mess.ratelimiter;

/**
 * @author penghuiping
 * @date 2022/7/20 20:08
 */
public interface RateLimiter {

    /**
     * 是否可以访问
     *
     * @return true: 是否
     */
    Boolean isAllowed();
}
