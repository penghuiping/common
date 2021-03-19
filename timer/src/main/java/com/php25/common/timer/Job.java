package com.php25.common.timer;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.RandomUtil;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.text.ParseException;
import java.util.Date;

/**
 * @author penghuiping
 * @date 2021/3/12 21:23
 */
public class Job implements TimerTask {
    /**
     * 任务执行时间
     */
    private final long executeTime;

    /**
     * 此任务执行id
     */
    private final String jobExecutionId;

    /**
     * cron表达式
     */
    private final String cron;

    /**
     * 具体需要执行的任务
     */
    private final Runnable task;

    public Job(String cron, Runnable task) {
        this(RandomUtil.randomUUID(), cron, task);
    }


    public Job(String jobExecutionId, String cron, Runnable task) {
        try {
            this.executeTime = new CronExpression(cron).getNextValidTimeAfter(new Date()).getTime();
        } catch (ParseException e) {
            throw new CronException("cron 表达式不正确", e);
        }
        this.jobExecutionId = jobExecutionId;
        this.cron = cron;
        this.task = task;
    }

    public Job(String jobExecutionId, long executeTime, Runnable task) {
        this.executeTime = executeTime;
        this.jobExecutionId = jobExecutionId;
        this.task = task;
        this.cron = null;
    }


    public long getExecuteTime() {
        return executeTime;
    }

    public String getJobExecutionId() {
        return jobExecutionId;
    }

    public String getCron() {
        return cron;
    }

    public Runnable getTask() {
        return task;
    }

    public long getDelay() {
        return this.executeTime - System.currentTimeMillis();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        Job job0 = (Job) timeout.task();
        TimerInnerLogManager jobExecutionLogManager = SpringContextHolder.getBean0(TimerInnerLogManager.class);
        jobExecutionLogManager.synchronizedExecutionJob(job0);
    }
}
