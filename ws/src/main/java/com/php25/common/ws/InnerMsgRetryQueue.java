package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ConcurrentHashMap<String, BaseRetryMsg> msgs = new ConcurrentHashMap<>(1024);

    private MsgDispatcher msgDispatcher;

    private ExecutorService singleThreadExecutor;

    public InnerMsgRetryQueue(MsgDispatcher msgDispatcher) {
        this.msgDispatcher = msgDispatcher;
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
        this.singleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("cpicwx-healthy-delay-queue-subscriber");
            return thread;
        });

        this.singleThreadExecutor.submit(() -> {
            while (true) {
                BaseRetryMsg msg = null;
                try {
                    msg = delayQueue.take();
                    if (null != msg) {
                        if (msg.getCount() < msg.getMaxRetry()) {
                            msgDispatcher.dispatch(msg);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("消息重发出错:{}", JsonUtil.toJson(msg), e);
                }
            }
        });
    }


    public void put(BaseRetryMsg baseRetry) {
        delayQueue.put(baseRetry);
        msgs.put(baseRetry.getMsgId() + baseRetry.getAction(), baseRetry);
    }

    public void remove(BaseRetryMsg baseRetry) {
        delayQueue.remove(baseRetry);
        msgs.remove(baseRetry.getMsgId() + baseRetry.getAction());
    }

    public BaseRetryMsg get(String msgId, String action) {
        return msgs.get(msgId + action);
    }

}
