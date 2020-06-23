package com.php25.timetasks.timewheel;

import com.php25.timetasks.exception.TimeTasksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author penghuiping
 * @date 2020/6/19 16:38
 */
public class JobDao {
    private static final Logger log = LoggerFactory.getLogger(JobDao.class);

    private DataSource dataSource;

    public JobDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(TimeTask timeTask) {
        LocalDateTime time = LocalDateTime.of(timeTask.year, timeTask.month, timeTask.day, timeTask.hour, timeTask.minute, timeTask.second);
        String className = timeTask.task.getClass().getName();
        String sql = "insert into t_time_task(`id`,`class_name`,`execute_time`,`cron`)values (?,?,?,?)";
        log.debug(sql);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);) {
            statement.setString(1, timeTask.getJobId());
            statement.setString(2, className);
            statement.setTimestamp(3, Timestamp.valueOf(time));
            statement.setString(4, timeTask.getCron());
            statement.execute();
        } catch (Exception e) {
            throw new TimeTasksException("SQL执行失败!", e);
        }
    }


    public void deleteAll(List<String> jobIds) {
        StringBuilder sb = new StringBuilder();
        jobIds.forEach(jobId -> {
            sb.append("\"").append(jobId).append("\"").append(",");
        });
        String sqlIn = sb.substring(0,sb.length()-1);
        String sql = String.format("delete from t_time_task where id in (%s)", sqlIn);
        log.debug(sql);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.execute();
        } catch (Exception e) {
            throw new TimeTasksException("SQL执行失败!", e);
        }
    }

    public void deleteAllInvalidJob() {
        String sql = "delete from t_time_task where execute_time < ?";
        log.debug(sql);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.execute();
        } catch (Exception e) {
            throw new TimeTasksException("SQL执行失败!", e);
        }
    }

    public List<TimeTask> findByExecuteTimeScope(LocalDateTime start, LocalDateTime end) {
        String sql = "select * from t_time_task a where a.execute_time between ? and ?";
        log.debug(sql);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setTimestamp(1, Timestamp.valueOf(start));
            statement.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = statement.executeQuery();
            List<TimeTask> timeTasks = new ArrayList<>();
            while (rs.next()) {
                String jobId = rs.getString(1);
                String className = rs.getString(2);
                LocalDateTime executeTime = rs.getTimestamp(3).toLocalDateTime();
                String cron = rs.getString(4);
                log.debug("timeTasks jobId:{}",jobId);
                Runnable runnable = (Runnable) Class.forName(className).newInstance();
                TimeTask timeTask = new TimeTask(executeTime, runnable, jobId,cron);
                timeTasks.add(timeTask);
            }
            rs.close();
            return timeTasks;
        } catch (Exception e) {
            throw new TimeTasksException("SQL执行失败!", e);
        }
    }


    public String generateJobId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
