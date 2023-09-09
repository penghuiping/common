package com.php25.common.timer.config;

/**
 * @author penghuiping
 * @date 2023/9/9 11:34
 */
public enum TimerType {
    /**
     * 使用redis来实现锁机制
     */
    USING_REDIS,
    /**
     * 使用database来实现锁机制
     */
    USING_DATABASE
}
