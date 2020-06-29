package com.php25.timetasks.cron;

import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

/**
 * @author penghuiping
 * @date 2020/6/28 17:30
 */
abstract class Symbol {

    /**
     * 可能的时间数字
     */
    protected IntStream possibleTimeValues;

    /**
     * 时间单位，年、月、日、时、分、秒
     */
    protected ChronoUnit unit;

    /**
     * 一个月的第几周，-1:最后一周,0:全部,1:第一周
     */
    protected int weekOfMonth = 0;

    protected Symbol previous;
}
