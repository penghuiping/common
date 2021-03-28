package com.php25.common.timer;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.mess.SpringContextHolder;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.StringUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.redis.RedisManager;
import com.php25.common.timer.model.TimerInnerLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * @author penghuiping
 * @date 2021/3/19 10:53
 */
public class TimerInnerLogManager {
    private static final Logger log = LoggerFactory.getLogger(TimerInnerLogManager.class);

    private final DbType dbType;

    private final JdbcTemplate jdbcTemplate;

    private final RedisManager redisManager;

    public TimerInnerLogManager(DbType dbType, JdbcTemplate jdbcTemplate, RedisManager redisManager) {
        this.dbType = dbType;
        this.jdbcTemplate = jdbcTemplate;
        this.redisManager = redisManager;
    }

    void synchronizedExecutionJob(Job task) {
        Lock lock = redisManager.lock(task.getJobExecutionId() + ":" + task.getExecuteTime());
        String cron = task.getCron();
        String executionId = task.getJobExecutionId();
        Long executionTime = task.getExecuteTime();
        SqlParams sqlParams = Queries.of(dbType).from(TimerInnerLog.class)
                .whereEq("id", executionId)
                .andEq("executionTime", executionTime).single();
        sqlParams.setSql(sqlParams.getSql());
        TimerInnerLog jobExecutionLog = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
        if (null != jobExecutionLog && jobExecutionLog.getStatus() == 0) {
            lock.lock();
            try {
                jobExecutionLog = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams);
                if (jobExecutionLog.getStatus() == 0) {
                    //job执行计划没有执行过需要执行
                    LoggerFactory.getLogger(TimerInnerLogManager.class).info("执行任务:{}:{}", executionId, task.getExecuteTime());
                    task.getTask().run();
                    //更新执行状态为已执行
                    TimerInnerLog jobExecutionLog0 = new TimerInnerLog();
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

    void create(TimerInnerLog jobExecutionLog) {
        Lock lock = redisManager.lock(jobExecutionLog.getId() + ":" + jobExecutionLog.getExecutionTime());
        SqlParams sqlParams0 = Queries.of(dbType).from(TimerInnerLog.class)
                .whereEq("id", jobExecutionLog.getId())
                .andEq("executionTime", jobExecutionLog.getExecutionTime())
                .single();
        sqlParams0.setSql(sqlParams0.getSql());
        TimerInnerLog jobExecutionLog0 = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams0);
        if (null == jobExecutionLog0) {
            lock.lock();
            try {
                jobExecutionLog0 = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).single(sqlParams0);
                if (null == jobExecutionLog0) {
                    //查不到才新增
                    log.info("无法查询到:{}", JsonUtil.toJson(jobExecutionLog));
                    SqlParams sqlParams = Queries.of(dbType).from(TimerInnerLog.class).insert(jobExecutionLog);
                    QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).insert(sqlParams);
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

    void update(TimerInnerLog jobExecutionLog) {
        TimerInnerLog timerInnerLog = new TimerInnerLog();
        timerInnerLog.setStatus(jobExecutionLog.getStatus());
        SqlParams sqlParams = Queries.of(dbType).from(TimerInnerLog.class)
                .whereEq("id", jobExecutionLog.getId())
                .andEq("executionTime", jobExecutionLog.getExecutionTime())
                .update(timerInnerLog);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate).update(sqlParams);
    }
}
