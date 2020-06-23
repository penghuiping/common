package com.php25.timetasks.timewheel;

import com.php25.timetasks.exception.TimeTasksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
/**
 * @author penghuiping
 * @date 2020/6/20 20:38
 */
public class DbFactory {

    private static final Logger log = LoggerFactory.getLogger(DbFactory.class);
    private static final SQLiteDataSource datasource;

    private static final JobDao jobDao;

    static {
        datasource = new SQLiteDataSource();
        datasource.setUrl("jdbc:sqlite:/tmp/time_task.db");
        try {
            Connection connection = datasource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists t_time_task (`id` varchar(32) primary key,`class_name` varchar(100),`execute_time` datetime,`cron` varchar(64))");
            statement.closeOnCompletion();
            connection.close();
        }catch (Exception e) {
            throw new TimeTasksException("初始化数据库表失败",e);
        }
        jobDao = new JobDao(DbFactory.datasource);
    }

    public static DataSource getDatasource() {
        return DbFactory.datasource;
    }

    public static JobDao getJobDao() {
        return DbFactory.jobDao;
    }


}
