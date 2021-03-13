package com.php25.common.timer.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/6/22 17:24
 */
public class TestJob1 implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TestJob1.class);

    @Override
    public void run() {
        log.info("执行了一条语句:每10秒执行一次");
    }
}
