package com.php25.common.core.util;

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

    public static final String STD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 把日期类型的字符串，转换成日期类型
     *
     * @param dateStr           日期字符串 如:"2020-10-01"
     * @param dateTimeFormatter 解析的日期格式 如:"yyyy-MM-dd"
     * @return 日期时间
     */
    public static Date parseDate(String dateStr, DateTimeFormatter dateTimeFormatter) {
        return Date.from(LocalDateTime.parse(dateStr, dateTimeFormatter).toInstant(ZoneOffset.ofHours(8)));
    }

    /**
     * 把日期类型的字符串，转换成日期类型
     *
     * @param dateStr           日期字符串 如:"2020-10-01"
     * @param dateTimeFormatter 解析的日期格式 如:"yyyy-MM-dd"
     * @return 日期时间
     */
    public static LocalDateTime parseDateString(String dateStr, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(dateStr, dateTimeFormatter);
    }

    /**
     * 把时间戳转成字符串
     *
     * @param timeInMillis      UTC时间戳
     * @param dateTimeFormatter 解析的日期格式 如:"yyyy-MM-dd"
     * @return 日期字符串  如:"2020-10-01"
     */
    public static String getTime(long timeInMillis, DateTimeFormatter dateTimeFormatter) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 把日期转成字符串
     *
     * @param date              日期
     * @param dateTimeFormatter 格式:如 yyyy-MM-dd hh:mm:ss
     * @return 按照格式返回 字符串表示的时间
     */
    public static String getTime(Date date, DateTimeFormatter dateTimeFormatter) {
        return getTime(date.getTime(), dateTimeFormatter);
    }

    /**
     * 获取当前时间戳
     *
     * @return UTC时间戳
     */
    public static long getCurrentTimeMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * 获取当地时间
     *
     * @param timeMillis UTC时间戳
     * @return 当地时间
     */
    public static LocalDateTime fromTimeMillis(long timeMillis) {
        return Instant.ofEpochMilli(timeMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
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
     * @param date 日期时间
     * @return 当地时间
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date   日期时间
     * @param zoneId 时区
     * @return 当地时间
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

    /**
     * 时间1是否早于时间2
     *
     * @param first  时间1
     * @param second 时间2
     * @return true:时间1早于时间2,false:时间1晚于时间2
     */
    public static boolean isBefore(Date first, Date second) {
        return toLocalDateTime(first).isBefore(toLocalDateTime(second));
    }

    /**
     * 时间1是否晚于时间2
     *
     * @param first  时间1
     * @param second 时间2
     * @return true:时间1晚于时间2,false:时间1早于时间2
     */
    public static boolean isAfter(Date first, Date second) {
        return toLocalDateTime(first).isAfter(toLocalDateTime(second));
    }

    /**
     * 计算当年一个月第几周的第几天的日期
     *
     * @param dayOfWeek   一周的第几天
     * @param weekOfMonth 一月的第几周
     * @param month       第几个月  1~12
     * @return 对应的日期
     */
    public static LocalDateTime getWeekDayOfMonth(int dayOfWeek, int weekOfMonth, int month) {
        return getWeekDayOfMonth(LocalDateTime.now().getYear(), dayOfWeek, weekOfMonth, month);
    }

    /**
     * 计算某年一个月第几周的第几天的日期
     *
     * @param year        年
     * @param dayOfWeek   一周的第几天
     * @param weekOfMonth 一个月的第几周
     * @param month       第几个月  1~12
     * @return
     */
    public static LocalDateTime getWeekDayOfMonth(int year, int dayOfWeek, int weekOfMonth, int month) {
        LocalDateTime firstDayOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime nextWeek = firstDayOfMonth.plusWeeks(weekOfMonth - 1);
        LocalDateTime firstDayOfWeek = nextWeek.minusDays(nextWeek.getDayOfWeek().getValue());
        LocalDateTime result = firstDayOfWeek.plusDays(dayOfWeek - 1);
        return result;
    }

    /**
     * 计算某年一个月倒数第几周的第几天的日期
     *
     * @param year        年
     * @param dayOfWeek   一周的第几天
     * @param weekOfMonth 一个月的第几周
     * @param month       第几个月  1~12
     * @return
     */
    public static LocalDateTime getWeekDayOfMonthReverse(int year, int dayOfWeek, int weekOfMonth, int month) {
        int maxDay = getLastDayOfMonth(year, month);
        LocalDateTime endDayOfMonth = LocalDateTime.of(year, month, maxDay, 0, 0);
        LocalDateTime nextWeek = endDayOfMonth.plusWeeks(1 - weekOfMonth);
        LocalDateTime firstDayOfWeek = nextWeek.minusDays(nextWeek.getDayOfWeek().getValue());
        LocalDateTime result = firstDayOfWeek.plusDays(dayOfWeek - 1);
        return result;
    }

    /**
     * 计算某年中的某月的最后一天
     *
     * @param year  年
     * @param month 月
     * @return 最后一天 28~31中的一位数
     */
    public static int getLastDayOfMonth(int year, int month) {
        LocalDateTime time = LocalDateTime.of(year, month, 1, 0, 0);
        boolean leapYear = time.getChronology().isLeapYear(time.getYear());
        int maxDay = time.getMonth().length(leapYear);
        return maxDay;
    }
}
