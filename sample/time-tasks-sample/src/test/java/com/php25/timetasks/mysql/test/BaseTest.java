package com.php25.timetasks.mysql.test;

import com.php25.common.core.mess.IdGenerator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author: penghuiping
 * @date: 2020/8/25 10:27
 */
public class BaseTest {

    @Autowired
    IdGenerator idGenerator;

    @Autowired
    JdbcTemplate jdbcTemplate;




    private void initMeta() throws Exception {
        Class cls = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) cls.newInstance();
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "root");
        Connection connection = driver.connect("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false", properties);
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_time_task");
        statement.execute("create table t_time_task (id varchar(32) primary key,class_name varchar(1024),execute_time datetime,cron varchar(255),enable int)");
        statement.close();
        connection.close();
    }


    @Before
    public void before() throws Exception {
        initMeta();
    }
}
