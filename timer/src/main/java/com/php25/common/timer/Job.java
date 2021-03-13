package com.php25.common.timer;

import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.timer.manager.JobManager;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.util.Date;

/**
 * @author penghuiping
 * @date 2021/3/12 21:23
 */
@Log4j2
public class Job implements TimerTask {
    /**
     * 任务执行时间
     */
    private final long executeTime;

    /**
     * 此任务
     */
    private final String jobId;

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

    public Job(String jobId, String cron, Runnable task) {
        try {
            this.executeTime = new CronExpression(cron).getNextValidTimeAfter(new Date()).getTime();
        } catch (ParseException e) {
            throw new CronException("cron 表达式不正确", e);
        }
        this.jobId = jobId;
        this.cron = cron;
        this.task = task;
    }


    public long getExecuteTime() {
        return executeTime;
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

    public long getDelay() {
        return this.executeTime - System.currentTimeMillis();
    }

    @Override
    public void run(Timeout timeout) throws Exception {
        Job job0 = (Job) timeout.task();
        Timer timer = SpringContextHolder.getBean0(Timer.class);
        JobManager jobManager = SpringContextHolder.getBean0(JobManager.class);
        boolean enabled = jobManager.isEnable(job0.getJobId());
        if (enabled) {
            job0.getTask().run();
            Job job = new Job(job0.getJobId(), this.cron, this.task);
            timer.add(job);
        }
    }
}
