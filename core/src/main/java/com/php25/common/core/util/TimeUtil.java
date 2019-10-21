package com.php25.common.core.util;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 修改使用jdk1.8中的TimeApi
 * 时间处理帮助类
 *
 * @author penghuiping
 * @date 2018/7/2.
 */
public abstract class TimeUtil {
    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * 把日期类型的字符串，转换成日期类型
     *
     * @param dateStr
     * @return
     * @author penghuiping
     * @date 2014/8/13.
     */
    public static Date parseDate(String dateStr) {
        try {
            return DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMddHHmmss");
        } catch (ParseException e) {
            logger.error("出错啦!", e);
            return null;
        }
    }

    /**
     * 把日期类型的字符串，转换成日期类型
     *
     * @param dateStr
     * @return
     * @author penghuiping
     * @date 2014/8/13.
     */
    public static Date parseDate(String dateStr, DateTimeFormatter dateTimeFormatter) {
        return Date.from(LocalDateTime.parse(dateStr, dateTimeFormatter).toInstant(ZoneOffset.ofHours(8)));
    }


    /**
     * 把时间戳转成字符串
     *
     * @param timeInMillis
     * @param dateTimeFormatter
     * @return
     */
    public static String getTime(long timeInMillis, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 把日期转成字符串
     *
     * @param date
     * @param dateTimeFormatter
     * @return
     */
    public static String getTime(Date date, DateTimeFormatter dateTimeFormatter) {
        return getTime(date.getTime(), dateTimeFormatter);
    }


    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static long getCurrentTimeMillis() {
        return Clock.systemDefaultZone().millis();
    }


    /**
     * 获取某天的开始时间
     *
     * @param date 日期
     * @return 某天的开始时间
     */
    public static Date getBeginTimeOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 0, 0, 0);
        return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
    }

    /**
     * 获取某天的结束时间
     *
     * @param date 日期
     * @return 某天的结束时间
     */
    public static Date getEndTimeOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), 23, 59, 59);
        return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
    }

    /**
     * 偏移天
     *
     * @param date   日期
     * @param offset 偏移天数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetDay(Date date, int offset) {
        return offsetDate(date, ChronoUnit.DAYS, offset);
    }

    /**
     * 偏移周
     *
     * @param date   日期
     * @param offset 偏移周数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetWeek(Date date, int offset) {
        return offsetDate(date, ChronoUnit.WEEKS, offset);
    }

    /**
     * 偏移月
     *
     * @param date   日期
     * @param offset 偏移月数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static Date offsetMonth(Date date, int offset) {
        return offsetDate(date, ChronoUnit.MONTHS, offset);
    }

    /**
     * 获取指定日期偏移指定时间后的时间
     *
     * @param date       基准日期
     * @param chronoUnit 偏移的粒度大小（小时、天、月等）使用Calendar中的常数
     * @param offset     偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offsetDate(Date date, ChronoUnit chronoUnit, int offset) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime = localDateTime.plus(offset, chronoUnit);
        return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
    }


    /**
     * Date 转 LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date   日期时间
     * @param zoneId 时区
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }
}
