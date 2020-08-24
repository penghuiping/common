package com.php25.common.ws;

import com.php25.common.core.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.DelayQueue;

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
public class InnerMsgRetryQueue implements InitializingBean {

    private DelayQueue<BaseRetryMsg> delayQueue = new DelayQueue<>();

    private MsgDispatcher msgDispatcher;

    public InnerMsgRetryQueue(MsgDispatcher msgDispatcher) {
        this.msgDispatcher = msgDispatcher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        run();
    }

    public void run() {
        Thread thread = new Thread(() -> {
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
        thread.setName("cpicwx-healthy-delay-queue-subscriber");
        thread.start();
    }




    public void put(BaseRetryMsg baseRetry) {
        delayQueue.put(baseRetry);
    }

    public void remove(BaseRetryMsg baseRetry) {
        delayQueue.remove(baseRetry);
    }


}