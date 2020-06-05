package com.php25.timetasks;

import com.php25.timetasks.timewheel.RoundScope;
import com.php25.timetasks.timewheel.TimeTask;
import com.php25.timetasks.timewheel.TimeWheel;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author penghuiping
 * @date 2020/5/18 14:03
 */
public class TimeWheelTest {

    @Test
    public void test() {
        TimeWheel timeWheel = new TimeWheel(RoundScope.MINUTE);
        timeWheel.start();

        AtomicInteger count = new AtomicInteger(0);
        TimeTask timeTask1 = new TimeTask(2020, 5, 15, 17, 53, 30, () -> {
            System.out.println(LocalDateTime.now() + " hello1 "+ count.addAndGet(1));
        });
        timeWheel.add(timeTask1);

        TimeTask timeTask2 = new TimeTask(2020, 5, 15, 17, 54, 30, () -> {
            System.out.println(LocalDateTime.now() + " hello2 ");
        });
        timeWheel.add(timeTask2);

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        timeWheel.stop();
    }
}
