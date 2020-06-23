package com.php25.timetasks.timewheel;

import java.time.LocalDateTime;

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
     * 任务id
     */
    String jobId;

    /**
     * cron表达式
     */
    String cron;

    /**
     * 具体需要执行的任务
     */
    Runnable task;


    public TimeTask(LocalDateTime executeTime, Runnable task, String jobId,String cron) {
        this.year = executeTime.getYear();
        this.month = executeTime.getMonth().getValue();
        this.day = executeTime.getDayOfMonth();
        this.second = executeTime.getSecond();
        this.minute = executeTime.getMinute();
        this.hour = executeTime.getHour();
        this.task = task;
        this.jobId = jobId;
        this.cron = cron;
    }

    public String getJobId() {
        return jobId;
    }

    public String getCron() {
        return cron;
    }

    public Runnable getTask() {
        return task;
    }
}
