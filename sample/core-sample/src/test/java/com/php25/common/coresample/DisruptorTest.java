package com.php25.common.coresample;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.junit.Test;
/**
 * @author penghuiping
 * @date 2020/9/8 14:13
 */
public class DisruptorTest {

    public class TestEvent {

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "TestEvent{" +
                    "msg='" + msg + '\'' +
                    '}';
        }
    }


    @Test
    public void test()  throws Exception{
        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<TestEvent> disruptor = new Disruptor<>(TestEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        // Connect the handler
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event));

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<TestEvent> ringBuffer = disruptor.getRingBuffer();
        for (long l = 0; l<100000; l++)
        {
            String msg = ""+l;
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setMsg(msg));
        }
    }
}
