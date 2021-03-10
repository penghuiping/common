package com.php25.common.ws;

import com.google.common.base.Objects;
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

    public ExpirationSessionId(String sessionId, long timestamp) {
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    /**
     * 默认30秒没有收到心跳，断开连接
     */
    private final long timeout = 30000;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpirationSessionId that = (ExpirationSessionId) o;
        return timestamp == that.timestamp && Objects.equal(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId, timestamp);
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
