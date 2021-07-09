package com.php25.common.redis.local;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author penghuiping
 * @date 2021/3/3 10:18
 */
class Constants {
    /**
     * 默认缓存过期时间 2099-12-31 23:59:59
     */
    static final Long DEFAULT_EXPIRED_TIME = LocalDateTime.of(2099, 12, 31, 23, 59, 59).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

    static final int TIME_OUT = 5;

}
