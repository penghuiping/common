package com.php25.common.ws;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/8/17 14:03
 */
@Slf4j
public class HeartBeatWorker implements InitializingBean, DisposableBean {

    private final Long interval;

    private final GlobalSession globalSession;

    private ExecutorService singleThreadExecutor;

    public HeartBeatWorker(Long interval, GlobalSession globalSession) {
        this.interval = interval;
        this.globalSession = globalSession;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    @Override
    public void destroy() throws Exception {
        this.singleThreadExecutor.shutdown();
    }

    public void run() {

        this.singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactoryBuilder().setNameFormat("ws-heartbeat-worker-%d")
                        .build());

        this.singleThreadExecutor.submit(() -> {
            log.info("heart beat thread start...");
            while (true) {
                DelayQueue<ExpirationSessionId> delayQueue = globalSession.getAllExpirationSessionIds();
                while (true) {
                    try {
                        ExpirationSessionId expirationSessionId = delayQueue.poll(interval, TimeUnit.MILLISECONDS);
                        if (null == expirationSessionId) {
                            //优化 移除一些已经不需要的项
                            ConcurrentHashMap<String, ExpirationSocketSession> sessions = globalSession.getAllExpirationSessions();
                            while (true) {
                                ExpirationSessionId expirationSessionId0 = delayQueue.peek();
                                if (null == expirationSessionId0) {
                                    break;
                                }
                                ExpirationSocketSession expirationSocketSession0 = sessions.get(expirationSessionId0.getSessionId());
                                if (expirationSessionId0.getTimestamp() == expirationSocketSession0.getTimestamp()) {
                                    break;
                                }
                                delayQueue.remove(expirationSessionId0);
                            }
                            break;
                        }
                        ConcurrentHashMap<String, ExpirationSocketSession> sessions = globalSession.getAllExpirationSessions();
                        ExpirationSocketSession expirationSocketSession = sessions.get(expirationSessionId.getSessionId());
                        if (expirationSessionId.getTimestamp() == expirationSocketSession.getTimestamp()) {
                            ConnectionClose connectionClose = new ConnectionClose();
                            connectionClose.setCount(1);
                            connectionClose.setMsgId(globalSession.generateUUID());
                            connectionClose.setSessionId(expirationSessionId.getSessionId());
                            globalSession.send(connectionClose);
                        }
                    } catch (InterruptedException e) {
                        log.info("HeartBeatWorker心跳线程睡眠被打断", e);
                    }
                }

            }
        });
    }
}
