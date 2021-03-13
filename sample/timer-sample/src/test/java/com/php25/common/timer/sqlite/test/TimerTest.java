package com.php25.common.timer.sqlite.test;

import com.php25.common.db.DbType;
import com.php25.common.timer.CommonAutoConfigure;
import com.php25.common.timer.Job;
import com.php25.common.timer.Timer;
import com.php25.common.timer.manager.JobManager;
import com.php25.common.timer.repository.JobModelRepository;
import com.php25.common.timer.sqlite.job.TestJob;
import com.php25.common.timer.sqlite.job.TestJob1;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

/**
 * @author penghuiping
 * @date 2020/8/25 10:00
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class TimerTest {

    private static final Logger log = LoggerFactory.getLogger(TimerTest.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DbType dbType;

    @Autowired
    JobManager jobManager;

    @Autowired
    JobModelRepository jobModelRepository;

    @Before
    public void before() {
        jdbcTemplate.execute("drop table if exists t_timer_job");
        jdbcTemplate.execute("create table t_timer_job (id varchar(32) primary key,class_name varchar(1024),cron varchar(255),enable int)");
        Job job = new Job("55 * * * * ? 2021", new TestJob());
        Job job1 = new Job("0/10 * * * * ? 2021", new TestJob1());

        jobManager.create(job);
        jobManager.create(job1);

    }

    @Test
    public void test1() throws Exception {
        Timer timer = new Timer(jobManager);
        timer.start();
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()) {

        }
    }

}
