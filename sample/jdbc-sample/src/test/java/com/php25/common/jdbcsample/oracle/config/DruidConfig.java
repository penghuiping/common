package com.php25.common.jdbcsample.oracle.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbc.DbType;
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
public class DruidConfig {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.h2.Driver");
        druidDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE;");
        druidDataSource.setUsername("");
        druidDataSource.setPassword("");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            LoggerFactory.getLogger(com.php25.common.jdbcsample.mysql.config.DruidConfig.class).error("出错啦！", e);
        }
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    Db db(JdbcTemplate jdbcTemplate) {
        return new Db(jdbcTemplate, DbType.ORACLE);
    }
}
