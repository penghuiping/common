package com.php25.common.timer.test;

import com.php25.common.timer.CommonAutoConfigure;
import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        Job job = new Job("55 * * * * ? 2021", new TestJob());
        Job job1 = new Job("0/10 * * * * ? 2021", new TestJob1());
        timer.start();
        timer.add(job);
        timer.add(job1);
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) {

        }
    }

}
