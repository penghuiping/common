package com.php25.common.jpasample.mysql.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Configuration
public class DbConfig {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.h2.Driver");
        druidDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;");
        druidDataSource.setUsername("");
        druidDataSource.setPassword("");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            LoggerFactory.getLogger(DbConfig.class).error("出错啦！", e);
        }
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    Db db(JdbcTemplate jdbcTemplate) {
        return new Db(jdbcTemplate, DbType.MYSQL);
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }

    @Bean
    UidGenerator uidGenerator(SnowflakeIdWorker snowflakeIdWorker) {
        return new UidGenerator() {
            @Override
            public long getUID() throws UidGenerateException {
                return snowflakeIdWorker.nextId();
            }

            @Override
            public String parseUID(long uid) {
                return uid + "";
            }
        };
    }
}
