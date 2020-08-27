package com.php25.common.timetasks.mysql.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/6/22 17:24
 */
public class TestJob implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(TestJob.class);

    private String printMsg;

    public TestJob() {
    }

    public void setPrintMsg(String printMsg) {
        this.printMsg = printMsg;
    }

    @Override
    public void run() {
        log.info("执行了一条语句:{}",this.printMsg);
    }
}
