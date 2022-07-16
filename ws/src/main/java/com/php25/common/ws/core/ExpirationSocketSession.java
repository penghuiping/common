package com.php25.common.ws.core;

import com.google.common.base.Objects;
import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.ws.protocal.BaseMsg;
import com.php25.common.ws.protocal.ConnectionClose;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 可过期的WebSocket会话
 *
 * @author penghuiping
 * @date 2020/8/17 16:58
 */
@Setter
@Getter
@Slf4j
public class ExpirationSocketSession {

    private String sessionId;

    private WebSocketSession webSocketSession;

    private BlockingQueue<BaseMsg> buffer = new LinkedBlockingQueue<>(1024);

    private ExecutorService executorService;

    private MsgDispatcher msgDispatcher;

    private long timestamp;

    private Future<?> threadFuture;

    private SessionExpiredCallback callback;

    /**
     * 默认30秒没有收到心跳，断开连接
     */
    private long timeout = 30000;

    private AtomicBoolean isRunning = new AtomicBoolean(true);


    public ExpirationSocketSession(String sessionId, WebSocketSession webSocketSession, ExecutorService executorService, MsgDispatcher msgDispatcher) {
        this.sessionId = sessionId;
        this.webSocketSession = webSocketSession;
        this.executorService = executorService;
        this.msgDispatcher = msgDispatcher;
        this.timestamp = System.currentTimeMillis();
        this.callback = new SessionExpiredCallback(sessionId);
    }

    public void refreshTime() {
        this.timestamp = System.currentTimeMillis();
    }

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

    public void stop() {
        isRunning.compareAndSet(true, false);
    }

    public void put(BaseMsg baseMsg) {
        if (executorService.isShutdown()) {
            log.warn("线程池已经关闭,无法继续处理消息,:{}", JsonUtil.toJson(baseMsg));
            return;
        }

        boolean flag = buffer.offer(baseMsg);
        if (flag) {
            if (null == this.threadFuture || this.threadFuture.isDone()) {
                synchronized (this) {
                    if (null == this.threadFuture || this.threadFuture.isDone()) {
                        this.threadFuture = executorService.submit(() -> {
                            //最长等30秒
                            int count = 0;
                            while (isRunning.get()) {
                                try {
                                    BaseMsg baseMsg1 = buffer.poll(1, TimeUnit.SECONDS);
                                    if (null != baseMsg1) {
                                        msgDispatcher.dispatch(baseMsg1);
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
}

/**
 * session过期后的回调处理
 */
@Log4j2
class SessionExpiredCallback implements Runnable {

    private final String sessionId;

    private final SessionContext sessionContext;

    public SessionExpiredCallback(String sessionId) {
        this.sessionId = sessionId;
        this.sessionContext = SpringContextHolder.getBean0(SessionContext.class);
    }

    @Override
    public void run() {
        try {
            log.info("长时间未收到心跳包关闭ws连接");
            ConnectionClose connectionClose = new ConnectionClose();
            connectionClose.setMsgId(sessionContext.generateUUID());
            connectionClose.setSessionId(sessionId);
            ExpirationSocketSession expirationSocketSession = sessionContext.getExpirationSocketSession(sessionId);
            expirationSocketSession.put(connectionClose);
        } catch (Exception e) {
            log.error("未接受到心跳包，关闭session出错", e);
        }

    }
}
