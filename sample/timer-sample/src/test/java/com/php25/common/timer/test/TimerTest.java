package com.php25.common.timer.test;

import com.php25.common.core.util.TimeUtil;
import com.php25.common.timer.CommonAutoConfigure;
import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

/**
 * @author penghuiping
 * @date 2020/8/25 10:00
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class TimerTest {

    @Autowired
    private Timer timer;


    @Test
    public void test1() throws Exception {
        Runnable runnable0 = () -> {
            System.out.println(String.format("5秒触发一次,当前时间%s", TimeUtil.getTime(new Date(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        };
        Job job1 = new Job("*/5 * * * * ? *", runnable0);
        timer.start();
        timer.add(job1);

        Scanner scanner = new Scanner(System.in);
        scanner.next();
    }

}
