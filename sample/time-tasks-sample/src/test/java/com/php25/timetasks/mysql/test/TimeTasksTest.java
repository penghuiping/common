package com.php25.timetasks.mysql.test;

import com.google.common.collect.Lists;
import com.php25.timetasks.CommonAutoConfigure;
import com.php25.timetasks.TimeTasks;
import com.php25.timetasks.mysql.job.TestJob;
import com.php25.timetasks.timewheel.TimeWheel;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

/**
 * @author penghuiping
 * @date 2020/8/25 10:00
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class TimeTasksTest extends BaseTest {
    @ClassRule
    public static GenericContainer mysql = new GenericContainer<>("mysql:5.7").withExposedPorts(3306);


    static {
        mysql.setPortBindings(Lists.newArrayList("3306:3306"));
        mysql.withEnv("MYSQL_USER", "root");
        mysql.withEnv("MYSQL_ROOT_PASSWORD", "root");
        mysql.withEnv("MYSQL_DATABASE", "test");
    }

    @Autowired
    private TimeWheel timeWheel;

    @Test
    public void test() throws Exception{
        new Thread(()->{
            TimeTasks.submit(timeWheel,"0/5 * * 25 8 ? 2020",new TestJob("每5秒"));
        }).start();

        new Thread(()->{
            TimeTasks.submit(timeWheel,"0/30 * * 25 8 ? 2020",new TestJob("每30秒"));
        }).start();

        while (true) {
            Thread.sleep(1000);
        }
    }



}
