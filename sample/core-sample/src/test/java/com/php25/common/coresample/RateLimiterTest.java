package com.php25.common.coresample;

import com.php25.common.core.mess.ratelimiter.RateLimiter;
import com.php25.common.core.mess.ratelimiter.SlideWindowRateLimiter;
import com.php25.common.core.mess.ratelimiter.TokenBucketRateLimiter;
import com.php25.common.core.util.TimeUtil;
import org.junit.Test;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2022/7/18 23:33
 */
public class RateLimiterTest {

    @Test
    public void test() throws Exception {
        RateLimiter rateLimiter = new SlideWindowRateLimiter(1, 1, TimeUnit.SECONDS);
        int i = 0;
        for (; ; ) {
            boolean flag = rateLimiter.isAllowed();
            if (flag) {
                i++;
                System.out.println("是否允许访问:" + flag + "=====>" + TimeUtil.toLocalDateTime(new Date()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSSSS")) + "   count:" + i);
            }
            Thread.sleep(100);
        }
    }

    @Test
    public void test1() throws Exception {
        RateLimiter rateLimiter = new TokenBucketRateLimiter(8, 8, TimeUnit.SECONDS);
        int i = 0;
        for (; ; ) {
            boolean flag = rateLimiter.isAllowed();
            if (flag) {
                i++;
                System.out.println("是否允许访问:" + flag + "=====>" + TimeUtil.toLocalDateTime(new Date()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSSSS")) + "   count:" + i);

            }
            Thread.sleep(100);
        }
    }
}
