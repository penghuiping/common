package com.php25.common.ws;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author penghuiping
 * @date 2020/8/17 16:58
 */
@Setter
@Getter
@Slf4j
public class ExpirationSocketSession implements Delayed {

    private String sessionId;

    private WebSocketSession webSocketSession;

    private BlockingQueue<BaseRetryMsg> buffer = new LinkedBlockingQueue<>();

    private ExecutorService executorService;

    private MsgDispatcher msgDispatcher;

    private long timestamp;

    private Future<?> threadFuture;

    /**
     * 默认30秒没有收到心跳，断开连接
     */
    private long timeout = 30000;

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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

    public void stop() {
        isRunning.set(false);
    }

    public void put(BaseRetryMsg baseRetryMsg) {
        buffer.offer(baseRetryMsg);
        if (null == this.threadFuture || this.threadFuture.isDone()) {
            synchronized (this) {
                if (null == this.threadFuture || this.threadFuture.isDone()) {
                    this.threadFuture = executorService.submit(() -> {
                        //最长等30秒
                        int count = 0;
                        while (isRunning.get()) {
                            try {
                                BaseRetryMsg baseRetryMsg1 = buffer.poll(1, TimeUnit.SECONDS);
                                if (null != baseRetryMsg1) {
                                    msgDispatcher.dispatch(baseRetryMsg1);
                                    count = 0;
                                } else {
                                    count++;
                                    if (count >= 30) {
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                log.error("ExpirationSocketSession缓冲队列获取消息出错", e);
                            }
                        }
                        log.info("回收ws-worker线程");
                    });
                }
            }
        }
    }
}
