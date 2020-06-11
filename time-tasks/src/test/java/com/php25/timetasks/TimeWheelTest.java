package com.php25.timetasks;

import com.php25.timetasks.timewheel.TimeWheel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/5/18 14:03
 */
public class TimeWheelTest {
    private static final Logger log = LoggerFactory.getLogger(TimeWheelTest.class);

    @Test
    public void test() {
        String cron = "0/5 * * * * ? *";
        TimeWheel timeWheel = TimeTasks.startTimeWheel();
        TimeTasks.submit(timeWheel, cron, () -> {
            log.info("执行了一条语句");
        });

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
