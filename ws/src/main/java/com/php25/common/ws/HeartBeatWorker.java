package com.php25.common.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2020/8/17 14:03
 */
@Slf4j
public class HeartBeatWorker implements InitializingBean {

    private Long interval;

    private GlobalSession globalSession;

    public HeartBeatWorker(Long interval, GlobalSession globalSession) {
        this.interval = interval;
        this.globalSession = globalSession;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    public void run() {
        Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("cpicwx-healthy-heartbeat-worker");
            return thread;
        }).submit(() -> {
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
                        connectionClose.setSessionId( expirationSocketSession.getSessionId());
                        globalSession.send(connectionClose);
                    } catch (InterruptedException e) {
                        log.info("HeartBeatWorker心跳线程睡眠被打断", e);
                    }
                }

            }
        });
    }
}
