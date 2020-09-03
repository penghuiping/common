package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 消息重发器,此类用于实现延时消息重发
 * 对于没有收到ack的消息,需要重发,
 * 默认5秒没收到ack消息,进行原消息重发
 * 最大重发次数为5次
 *
 * @author penghuiping
 * @date 20/8/11 10:50
 */
@Slf4j
public class InnerMsgRetryQueue implements InitializingBean, DisposableBean {

    private DelayQueue<BaseRetryMsg> delayQueue = new DelayQueue<>();

    private BlockingQueue<BaseRetryMsg> noDelayQueue = new LinkedBlockingQueue<>();

    private ConcurrentHashMap<String, BaseRetryMsg> msgs = new ConcurrentHashMap<>(8196);

    private ExecutorService singleThreadExecutor;
    private ExecutorService singleThreadExecutorNoDelay;


    private GlobalSession globalSession;

    public InnerMsgRetryQueue() {
    }

    public void setGlobalSession(GlobalSession globalSession) {
        this.globalSession = globalSession;
    }


    @Override
    public void destroy() {
        this.singleThreadExecutor.shutdown();
        this.singleThreadExecutorNoDelay.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    public void run() {
        this.singleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("cpicwx-healthy-delay-queue-subscriber");
            return thread;
        });

        this.singleThreadExecutor.execute(() -> {
            while (true) {
                BaseRetryMsg msg = null;
                try {
                    msg = delayQueue.poll(2, TimeUnit.SECONDS);
                    if (null != msg) {
                        if (msg.getCount() < msg.getMaxRetry()) {
                            ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(msg.getSessionId());
                            if (null != expirationSocketSession) {
                                expirationSocketSession.put(msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("消息重发出错:{}", JsonUtil.toJson(msg), e);
                }
            }
        });

        this.singleThreadExecutorNoDelay = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("cpicwx-healthy-delay-queue-nodelay-subscriber");
            return thread;
        });

        this.singleThreadExecutorNoDelay.execute(() -> {
            while (true) {
                BaseRetryMsg msg = null;
                try {
                    msg = noDelayQueue.poll(2, TimeUnit.SECONDS);
                    if (null != msg) {
                        if (msg.getCount() < msg.getMaxRetry()) {
                            ExpirationSocketSession expirationSocketSession = globalSession.getExpirationSocketSession(msg.getSessionId());
                            if (null != expirationSocketSession) {
                                expirationSocketSession.put(msg);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("消息重发出错:{}", JsonUtil.toJson(msg), e);
                }
            }
        });
    }


    public void put(BaseRetryMsg baseRetry) {
        if (baseRetry.getInterval() > 0) {
            delayQueue.put(baseRetry);
        } else {
            noDelayQueue.offer(baseRetry);
        }
        msgs.put(baseRetry.getMsgId() + baseRetry.getAction(), baseRetry);
    }

    public void remove(BaseRetryMsg baseRetry) {
        if (baseRetry.getInterval() > 0) {
            delayQueue.remove(baseRetry);
        }
        msgs.remove(baseRetry.getMsgId() + baseRetry.getAction());
    }

    public BaseRetryMsg get(String msgId, String action) {
        return msgs.get(msgId + action);
    }
}
