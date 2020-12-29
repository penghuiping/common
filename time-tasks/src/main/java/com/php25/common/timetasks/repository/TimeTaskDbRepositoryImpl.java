package com.php25.common.timetasks.repository;

import com.php25.common.core.mess.IdGenerator;
import com.php25.common.db.Db;
import com.php25.common.db.repository.BaseDbRepositoryImpl;
import com.php25.common.timetasks.exception.TimeTasksException;
import com.php25.common.timetasks.model.TimeTaskDb;
import com.php25.common.timetasks.timewheel.TimeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/8/24 13:56
 */
@Repository
public class TimeTaskDbRepositoryImpl extends BaseDbRepositoryImpl<TimeTaskDb, String> implements TimeTaskDbRepository {

    @Autowired
    private IdGenerator idGenerator;

    public TimeTaskDbRepositoryImpl(Db db) {
        super(db);
    }

    @Override
    public void save(TimeTask timeTask) {
        LocalDateTime time = LocalDateTime.of(timeTask.getYear(),
                timeTask.getMonth(),
                timeTask.getDay(),
                timeTask.getHour(),
                timeTask.getMinute(),
                timeTask.getSecond());

        String className = timeTask.getTask().getClass().getName();

        TimeTaskDb timeTaskDb = new TimeTaskDb();
        timeTaskDb.setClassName(className);
        timeTaskDb.setCron(timeTask.getCron());
        timeTaskDb.setId(timeTask.getJobId());
        timeTaskDb.setExecuteTime(time);
        timeTaskDb.setEnable(1);
        timeTaskDb.setNew(true);
        this.save(timeTaskDb);
    }

    @Override
    public void deleteAll(List<String> jobIds) {
        List<TimeTaskDb> timeTaskDbs = jobIds.stream().map(id -> {
            TimeTaskDb timeTaskDb = new TimeTaskDb();
            timeTaskDb.setId(id);
            return timeTaskDb;
        }).collect(Collectors.toList());
        this.deleteAll(timeTaskDbs);
    }

    @Override
    public void deleteAllInvalidJob() {
        db.from(TimeTaskDb.class, "a").whereLess("a.executeTime", LocalDateTime.now()).delete();
    }

    @Override
    public List<TimeTask> findByExecuteTimeScope(LocalDateTime start, LocalDateTime end) {
        List<TimeTaskDb> timeTaskDbs = db.getBaseSqlExecute().select(db.from(TimeTaskDb.class).whereBetween("executeTime", start, end).select());
        List<TimeTask> timeTasks = timeTaskDbs.stream().map(timeTaskDb -> {
            try {
                Class<?> cls = Class.forName(timeTaskDb.getClassName());
                Runnable runnable = (Runnable) cls.newInstance();
                TimeTask timeTask = new TimeTask(timeTaskDb.getExecuteTime(), runnable, timeTaskDb.getId(), timeTaskDb.getCron());
                return timeTask;
            } catch (Exception e) {
                throw new TimeTasksException("查询给定时间范围内的任务出错啦!", e);
            }
        }).collect(Collectors.toList());
        return timeTasks;
    }

    @Override
    public String generateJobId() {
        return idGenerator.getUUID();
    }
}
