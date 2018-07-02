package com.php25.common;

import com.php25.common.util.TimeUtil;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/2 13:08
 * @Description:
 */
public class TimeUtilTest {

    @Test
    public void test() {
        Clock clock = Clock.systemDefaultZone();

        String localDate = LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(localDate.toString());


        Date startDate = TimeUtil.getBeginTimeOfDay(new Date());
        Date endDate = TimeUtil.getEndTimeOfDay(new Date());
        System.out.println(TimeUtil.getTime(startDate, DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")));
        System.out.println(TimeUtil.getTime(endDate, DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")));

        LocalDateTime tmp = LocalDateTime.parse("2018-07-02 13:56:40", DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));

        System.out.println(tmp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        System.out.println(TimeUtil.offsetDate(new Date(), ChronoUnit.DAYS, -7));


    }
}
