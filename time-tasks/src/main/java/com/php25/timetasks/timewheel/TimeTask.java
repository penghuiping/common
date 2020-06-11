package com.php25.timetasks.timewheel;

/**
 * 定时任务
 *
 * @author penghuiping
 * @date 2020/5/14 16:11
 */
public class TimeTask {
    /**
     * 前指针
     */
    TimeTask previous;

    /**
     * 后指针
     */
    TimeTask next;

    /**
     * 年
     */
    int year;

    /**
     * 月
     */
    int month;

    /**
     * 日
     */
    int day;

    /**
     * 类比钟表时针 0~23
     */
    int hour;

    /**
     * 类比钟表分针 0~59
     */
    int minute;

    /**
     * 类比钟表秒针 0~59
     */
    int second;


    /**
     * 具体需要执行的任务
     */
    Runnable task;

    public TimeTask(int year, int month, int day, int hour, int minute, int second, Runnable task) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.task = task;
    }
}
