package com.php25.common.coresample;

import com.php25.common.core.util.TimeUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(TimeUtilTest.class);

    @Test
    public void test() {
        Clock clock = Clock.systemDefaultZone();
        String localDate = LocalDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info(localDate);
        LocalDateTime tmp = LocalDateTime.parse("2018-07-02 13:56:40", DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        log.info(tmp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void test1() {
        Date startDate = TimeUtil.getBeginTimeOfDay(new Date());
        Date endDate = TimeUtil.getEndTimeOfDay(new Date());
        String value = TimeUtil.getTime(startDate, DateTimeFormatter.ofPattern("yyy-MM-dd"));
        Assertions.assertThat(value + " 00:00:00").isEqualTo(TimeUtil.getTime(startDate, DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")));
        Assertions.assertThat(value + " 23:59:59").isEqualTo(TimeUtil.getTime(endDate, DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")));
    }

    @Test
    public void test2() {
        log.info(TimeUtil.getTime(TimeUtil.offsetDate(new Date(), ChronoUnit.DAYS, -7),DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")));
    }
}
