package com.php25.common.ws;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/8/17 16:58
 */
@Setter
@Getter
public class ExpirationSocketSession implements Delayed {

    private String sessionId;

    private WebSocketSession webSocketSession;

    private long timestamp;

    /**
     * 默认30秒没有收到心跳，断开连接
     */
    private long timeout = 30000;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ExpirationSocketSession that = (ExpirationSocketSession) o;
        return Objects.equal(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId);
    }

    public synchronized long getTimestamp() {
        return timestamp;
    }

    public synchronized void setTimestamp(long timestamp) {
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
