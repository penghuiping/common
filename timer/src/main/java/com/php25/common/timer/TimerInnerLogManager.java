package com.php25.common.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.redis.RedisManager;
import com.php25.common.timer.dao.TimerInnerLogDao;
import com.php25.common.timer.dao.po.TimerInnerLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * @author penghuiping
 * @date 2021/3/19 10:53
 */
public class TimerInnerLogManager {
    private static final Logger log = LoggerFactory.getLogger(TimerInnerLogManager.class);


    private final TimerInnerLogDao timerInnerLogDao;

    private final RedisManager redisManager;

    public TimerInnerLogManager(TimerInnerLogDao timerInnerLogDao, RedisManager redisManager) {
        this.timerInnerLogDao = timerInnerLogDao;
        this.redisManager = redisManager;
    }

    void synchronizedExecutionJob(Job task) {
        Lock lock = redisManager.lock(task.getJobExecutionId() + ":" + task.getExecuteTime());
        String cron = task.getCron();
        String executionId = task.getJobExecutionId();
        Long executionTime = task.getExecuteTime();
        LambdaQueryWrapper<TimerInnerLogPo> lambdaQueryWrapper = new QueryWrapper<TimerInnerLogPo>().lambda();
        lambdaQueryWrapper.eq(TimerInnerLogPo::getId, executionId);
        lambdaQueryWrapper.eq(TimerInnerLogPo::getExecutionTime, executionTime);
        TimerInnerLogPo jobExecutionLog = timerInnerLogDao.selectOne(lambdaQueryWrapper);
        if (null != jobExecutionLog && jobExecutionLog.getStatus() == 0) {
            lock.lock();
            try {
                jobExecutionLog = timerInnerLogDao.selectOne(lambdaQueryWrapper);
                if (jobExecutionLog.getStatus() == 0) {
                    //job执行计划没有执行过需要执行
                    LoggerFactory.getLogger(TimerInnerLogManager.class).info("执行任务:{}:{}", executionId, task.getExecuteTime());
                    task.getTask().run();
                    //更新执行状态为已执行
                    TimerInnerLogPo jobExecutionLog0 = new TimerInnerLogPo();
                    jobExecutionLog0.setId(jobExecutionLog.getId());
                    jobExecutionLog0.setExecutionTime(jobExecutionLog.getExecutionTime());
                    jobExecutionLog0.setStatus(1);
                    update(jobExecutionLog0);
                }
            } finally {
                lock.unlock();
            }
        }

        //此执行任务已经执行,则先从timer中移除
        Timer timer = SpringContextHolder.getBean0(Timer.class);
        timer.removeCache(executionId);

        //如果是周期性计划任务，需要继续下一次job
        if (StringUtil.isBlank(cron)) {
            return;
        }
        Date date = null;
        try {
            date = new CronExpression(cron).getNextValidTimeAfter(new Date());
        } catch (ParseException e) {
            throw Exceptions.throwIllegalStateException("cron表达式解析出错", e);
        }
        if (null == date) {
            return;
        }
        Job job = new Job(executionId, cron, task.getTask());
        timer.add(job, true);
    }

    void create(TimerInnerLogPo jobExecutionLog) {
        Lock lock = redisManager.lock(jobExecutionLog.getId() + ":" + jobExecutionLog.getExecutionTime());
        LambdaQueryWrapper<TimerInnerLogPo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TimerInnerLogPo::getId, jobExecutionLog.getId());
        lambdaQueryWrapper.eq(TimerInnerLogPo::getExecutionTime, jobExecutionLog.getExecutionTime());
        TimerInnerLogPo jobExecutionLog0 = timerInnerLogDao.selectOne(lambdaQueryWrapper);
        if (null == jobExecutionLog0) {
            lock.lock();
            try {
                jobExecutionLog0 = timerInnerLogDao.selectOne(lambdaQueryWrapper);
                if (null == jobExecutionLog0) {
                    //查不到才新增
                    log.info("无法查询到:{}", JsonUtil.toJson(jobExecutionLog));
                    timerInnerLogDao.insert(jobExecutionLog);
                } else {
                    log.info("查询到:{}", JsonUtil.toJson(jobExecutionLog));
                }
            } finally {
                lock.unlock();
            }
        } else {
            log.info("查询到:{}", JsonUtil.toJson(jobExecutionLog));
        }
    }

    void update(TimerInnerLogPo timerInnerLog) {
        LambdaUpdateWrapper<TimerInnerLogPo> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
        lambdaQueryWrapper.eq(TimerInnerLogPo::getId, timerInnerLog.getId());
        lambdaQueryWrapper.eq(TimerInnerLogPo::getExecutionTime, timerInnerLog.getExecutionTime());
        lambdaQueryWrapper.set(TimerInnerLogPo::getStatus, timerInnerLog.getStatus());
        timerInnerLogDao.update(null, lambdaQueryWrapper);
    }
}
