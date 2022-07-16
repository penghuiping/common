package com.php25.common.ws;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.ws.core.RetryMsgManager;
import com.php25.common.ws.core.SessionContext;

/**
 * @author penghuiping
 * @date 2021/8/22 18:22
 */
public class MetricsStats {

    public void stats() {
        SpringContextHolder.getBean0(RetryMsgManager.class).stats();
        SpringContextHolder.getBean0(SessionContext.class).stats();
    }
}
