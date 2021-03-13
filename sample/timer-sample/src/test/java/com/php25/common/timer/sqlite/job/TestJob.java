package com.php25.common.timer.sqlite.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/6/22 17:24
 */
public class TestJob implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TestJob.class);

    @Override
    public void run() {
        log.info("执行了一条语句:第55秒执行一次");
    }
}
