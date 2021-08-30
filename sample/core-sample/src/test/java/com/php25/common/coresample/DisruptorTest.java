package com.php25.common.coresample;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author penghuiping
 * @date 2021/8/27 20:49
 */
public class DisruptorTest {

    @Test
    public void test() throws Exception {
        int bufferSize = 16;


        // 阻塞策略
        WaitStrategy strategy = new BlockingWaitStrategy();
        Disruptor<LongEvent> disruptor =
                new Disruptor<>(LongEvent::new, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, strategy);
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            System.out.println("Event: " + event.value);
            Thread.sleep(1000);
        });
        disruptor.start();
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        for (long l = 0; true; l++) {
            // 获取下一个可用位置的下标
            long sequence = ringBuffer.next();
            try {
                // 返回可用位置的元素
                LongEvent event = ringBuffer.get(sequence);
                // 设置该位置元素的值
                event.set(l);
            } finally {
                ringBuffer.publish(sequence);
            }
            Thread.sleep(10);
        }
    }

    @Test
    public void test1() throws Exception {
        // 阻塞策略
        WaitStrategy strategy = new BlockingWaitStrategy();
        RingBuffer<LongEvent> ringBuffer = RingBuffer.create(ProducerType.SINGLE, LongEvent::new, 16, strategy);

        BatchEventProcessor<LongEvent> batchEventProcessor =
                new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), (event, sequence, endOfBatch) -> {
                    System.out.println((Thread.currentThread().getName() + ":" + "Event: " + event.value));
                    Thread.sleep(1000);
                }
                );

        BatchEventProcessor<LongEvent> batchEventProcessor1 =
                new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), (event, sequence, endOfBatch) -> {
                    System.out.println(Thread.currentThread().getName() + ":" + "Event1: " + event.value);
                    Thread.sleep(1000);
                }
                );

        ringBuffer.addGatingSequences(batchEventProcessor.getSequence(), batchEventProcessor1.getSequence());
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(batchEventProcessor);
        executorService.execute(batchEventProcessor1);

        for (long l = 0; true; l++) {
            // 获取下一个可用位置的下标
            long sequence = ringBuffer.next();
            try {
                // 返回可用位置的元素
                LongEvent event = ringBuffer.get(sequence);
                // 设置该位置元素的值
                event.set(l);
            } finally {
                ringBuffer.publish(sequence);
                System.out.println("publish:" + l);
            }
            Thread.sleep(10);
        }

    }

    public class LongEvent {
        private long value;

        public void set(long value) {
            this.value = value;
        }
    }
}
