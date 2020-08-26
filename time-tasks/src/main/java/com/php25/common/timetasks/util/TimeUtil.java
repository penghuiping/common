package com.php25.common.timetasks.util;

import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/6/3 16:53
 */
public class TimeUtil {

    /**
     * 计算当年一个月第几周的第几天的日期
     *
     * @param dayOfWeek   一周的第几天
     * @param weekOfMonth 一月的第几周
     * @param month       第几个月  1~12
     * @return 对应的日期
     */
    public static LocalDateTime getWeekDayOfMonth(int dayOfWeek, int weekOfMonth, int month) {
       return getWeekDayOfMonth(LocalDateTime.now().getYear(),dayOfWeek,weekOfMonth,month);
    }

    /**
     *计算某年一个月第几周的第几天的日期
     *
     * @param year 年
     * @param dayOfWeek 一周的第几天
     * @param weekOfMonth 一个月的第几周
     * @param month  第几个月  1~12
     * @return
     */
    public static LocalDateTime getWeekDayOfMonth(int year,int dayOfWeek, int weekOfMonth, int month) {
        LocalDateTime firstDayOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime nextWeek = firstDayOfMonth.plusWeeks(weekOfMonth - 1);
        LocalDateTime firstDayOfWeek = nextWeek.minusDays(nextWeek.getDayOfWeek().getValue());
        LocalDateTime result = firstDayOfWeek.plusDays(dayOfWeek - 1);
        return result;
    }

    /**
     *计算某年一个月倒数第几周的第几天的日期
     *
     * @param year 年
     * @param dayOfWeek 一周的第几天
     * @param weekOfMonth 一个月的第几周
     * @param month  第几个月  1~12
     * @return
     */
    public static LocalDateTime getWeekDayOfMonthReverse(int year,int dayOfWeek, int weekOfMonth, int month) {
        int maxDay = getLastDayOfMonth( year,  month);
        LocalDateTime endDayOfMonth = LocalDateTime.of(year, month, maxDay, 0, 0);
        LocalDateTime nextWeek = endDayOfMonth.plusWeeks(1-weekOfMonth );
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
