package com.php25.common.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2019/12/5 15:43
 */
public class HolidayDelegate implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(HolidayDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        log.info("do something...");
    }
}
