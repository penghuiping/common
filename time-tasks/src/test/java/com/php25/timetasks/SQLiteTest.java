package com.php25.timetasks;

import org.junit.Test;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author penghuiping
 * @date 2020/6/13 21:50
 */
public class SQLiteTest {

    @Test
    public void test() throws Exception{
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:/tmp/time_task.db");
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_time_task");
        statement.execute("create table t_time_task (`id` long primary key,name varchar(100))");
        statement.closeOnCompletion();
        connection.close();

        connection = dataSource.getConnection();
        statement = connection.createStatement();
        statement.execute("insert into t_time_task(`id`,`name`) values(1,'jack')");
        statement.closeOnCompletion();
        connection.close();


        connection = dataSource.getConnection();
        PreparedStatement statement1 = connection.prepareStatement("select * from t_time_task");
        ResultSet  rs = statement1.executeQuery();

        while(rs.next()) {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            System.out.println(String.format("id:%s,name:%s", id,name));
        }
        statement1.closeOnCompletion();
        connection.close();
    }
}
