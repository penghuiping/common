package com.php25.common.timer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.timer.dao.po.TimerInnerLogPo;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/12 21:23
 */
public class Timer {

    private final HashedWheelTimer wheelTimer;

    private final Map<String, Timeout> cache = new ConcurrentHashMap<>(1024);

    public Timer() {
        this(100, TimeUnit.MILLISECONDS, 60 * 10);
    }

    public Timer(Integer tickDuration, TimeUnit timeUnit, Integer ticksPerWheel) {
        this.wheelTimer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat("timer-wheel-thread-%d").build(), tickDuration, timeUnit, ticksPerWheel);
    }

    public void start() {
        this.wheelTimer.start();
    }

    public void stopAll() {
        this.wheelTimer.stop();
    }

    public Long size() {
        return this.wheelTimer.pendingTimeouts();
    }

    public void add(Job job) {
        add(job, false);
    }

    public void add(Job job, Boolean isHighAvailable) {
        job.setHighAvailable(isHighAvailable);
        Timeout timeout = this.wheelTimer.newTimeout(job, job.getDelay(), TimeUnit.MILLISECONDS);
        Job job0 = (Job) timeout.task();
        cache.put(job0.getJobExecutionId(), timeout);
        if (isHighAvailable) {
            TimerInnerLogManager jobExecutionLogManager = SpringContextHolder.getBean0(TimerInnerLogManager.class);
            TimerInnerLogPo jobExecutionLog = new TimerInnerLogPo();
            jobExecutionLog.setId(job.getJobExecutionId());
            jobExecutionLog.setExecutionTime(job.getExecuteTime());
            jobExecutionLog.setStatus(0);
            jobExecutionLogManager.create(jobExecutionLog);
        }
    }

    public boolean stop(String jobExecutionId) {
        Timeout timeout = cache.remove(jobExecutionId);
        if (null != timeout) {
            return timeout.cancel();
        }
        return true;
    }

    public Set<String> getAllLoadedExecutionIds() {
        return cache.keySet();
    }

    void removeCache(String jobId) {
        cache.remove(jobId);
    }
}
