package com.php25.common.coresample;

import com.php25.common.core.mess.SlideWindowRateLimiter;
import com.php25.common.core.util.TimeUtil;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author penghuiping
 * @date 2022/7/18 23:33
 */
public class RateLimiterTest {

    @Test
    public void test() throws Exception {
        SlideWindowRateLimiter sideWindowRateLimiter = new SlideWindowRateLimiter(2, 1, ChronoUnit.SECONDS);
        int i = 0;
        for (; ; ) {
            boolean flag = sideWindowRateLimiter.isAllowed();
            if (flag) {
                i++;
                System.out.println("是否允许访问:" + flag + "=====>" + TimeUtil.toLocalDateTime(new Date()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSSSS")) + "   count:" + i);

            }
            Thread.sleep(100);
        }
    }
}
