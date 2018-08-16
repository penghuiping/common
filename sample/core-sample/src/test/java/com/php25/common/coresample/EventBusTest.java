package com.php25.common.coresample;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.php25.common.coresample.dto.Message;
import com.php25.common.coresample.dto.Message1;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 15:12
 * @Description:
 */
public class EventBusTest {

    private EventBus eventBus;

    @Before
    public void before() {
        this.eventBus = new EventBus();
        eventBus.register(this);
    }

    @Test
    public void test() throws Exception {
        Message1 message = new Message1();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            message.setType(1);
            message.setContent("hello world");
            message.setResult("");
            eventBus.post(message);
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
        System.out.println(message.getResult());
    }

    @Subscribe
    void handle1(Message message) {
        System.out.println("success execute");
        message.setResult("success");
    }

    @Subscribe
    void handle(Message1 message) {
        System.out.println("failed execute");
        message.setResult("failed");
    }
}
