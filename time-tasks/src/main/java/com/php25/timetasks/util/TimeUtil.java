package com.php25.timetasks.util;

import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/6/3 16:53
 */
public class TimeUtil {

    /**
     * 计算一个月第几周的第几天的日期
     *
     * @param dayOfWeek   一周的第几天
     * @param weekOfMonth 一月的第几周
     * @param month       第几个月  1~12
     * @return 对应的日期
     */
    public static LocalDateTime getWeekDayOfMonth(int dayOfWeek, int weekOfMonth, int month) {
        LocalDateTime firstDayOfMonth = LocalDateTime.of(LocalDateTime.now().getYear(), month, 1, 0, 0);
        LocalDateTime nextWeek = firstDayOfMonth.plusWeeks(weekOfMonth - 1);
        LocalDateTime firstDayOfWeek = nextWeek.minusDays(nextWeek.getDayOfWeek().getValue());
        LocalDateTime result = firstDayOfWeek.plusDays(dayOfWeek - 1);
        return result;
    }
}
