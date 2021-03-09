package com.php25.common.ws;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/9 09:22
 */
@Getter
public class ExpirationSessionId implements Delayed {

    private final String sessionId;

    private final long timestamp;
    /**
     * 默认30秒没有收到心跳，断开连接
     */
    private final long timeout = 30000;

    public ExpirationSessionId(String sessionId, long timestamp) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return (timeout - (System.currentTimeMillis() - getTimestamp())) * 1000000;
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
    }
}
