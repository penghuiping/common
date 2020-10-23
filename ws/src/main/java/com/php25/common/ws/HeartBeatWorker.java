package com.php25.common.ws;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

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
                DelayQueue<ExpirationSocketSession> delayQueue = globalSession.getAllExpirationSessions();
                while (true) {
                    try {
                        ExpirationSocketSession expirationSocketSession = delayQueue.poll(interval, TimeUnit.MILLISECONDS);
                        if (null == expirationSocketSession) {
                            break;
                        }
                        ConnectionClose connectionClose = new ConnectionClose();
                        connectionClose.setCount(1);
                        connectionClose.setMsgId(globalSession.generateUUID());
                        connectionClose.setSessionId(expirationSocketSession.getSessionId());
                        globalSession.send(connectionClose);
                    } catch (InterruptedException e) {
                        log.info("HeartBeatWorker心跳线程睡眠被打断", e);
                    }
                }

            }
        });
    }
}
