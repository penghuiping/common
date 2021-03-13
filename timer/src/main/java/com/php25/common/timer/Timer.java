package com.php25.common.timer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.php25.common.timer.manager.JobManager;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author penghuiping
 * @date 2021/3/12 21:23
 */
public class Timer {

    private final HashedWheelTimer wheelTimer;

    private final JobManager jobManager;

    private final Map<String, Timeout> cache = new ConcurrentHashMap<>(1024);

    public Timer(JobManager jobManager) {
        this.wheelTimer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat("timer-wheel-thread-%d").build(), 100, TimeUnit.MILLISECONDS, 60 * 10);
        this.jobManager = jobManager;
    }

    public void start() {
        this.loadJobFromDb();
        this.wheelTimer.start();
    }

    public void loadJobFromDb() {
        List<Job> jobs = this.jobManager.findAllEnabled();
        for (Job job : jobs) {
            this.add(job);
        }
    }

    public void stopAll() {
        this.wheelTimer.stop();
    }

    public void add(Job job) {
        Timeout timeout = this.wheelTimer.newTimeout(job, job.getDelay(), TimeUnit.MILLISECONDS);
        Job job0 = (Job) timeout.task();
        cache.put(job0.getJobId(), timeout);
    }

    public void stop(String jobId) {
        Timeout timeout = cache.remove(jobId);
        if (null != timeout) {
            timeout.cancel();
        }
    }
}
