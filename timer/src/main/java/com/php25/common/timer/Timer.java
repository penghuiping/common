package com.php25.common.timer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import java.util.Map;
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
        this.wheelTimer = new HashedWheelTimer(new ThreadFactoryBuilder().setNameFormat("timer-wheel-thread-%d").build(), 100, TimeUnit.MILLISECONDS, 60 * 10);
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
        Timeout timeout = this.wheelTimer.newTimeout(job, job.getDelay(), TimeUnit.MILLISECONDS);
        Job job0 = (Job) timeout.task();
        cache.put(job0.getJobId(), timeout);
    }

    public boolean stop(String jobId) {
        Timeout timeout = cache.remove(jobId);
        if (null != timeout) {
            return timeout.cancel();
        }
        return true;
    }
}
