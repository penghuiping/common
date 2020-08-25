package com.php25.common.jdbcsample.oracle.config;

import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Configuration
public class DruidConfig {

    @Bean
    public DataSource druidDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        hikariDataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
        hikariDataSource.setUsername("system");
        hikariDataSource.setPassword("oracle");
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setConnectionTimeout(30000);
        hikariDataSource.setIdleTimeout(300000);
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaxLifetime(1800000);
        hikariDataSource.setMaximumPoolSize(15);
        hikariDataSource.setPoolName("hikariDataSource");
        return hikariDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    Db db(JdbcTemplate jdbcTemplate) {
        Db db = new Db(DbType.ORACLE);
        db.setJdbcOperations(jdbcTemplate);
        db.scanPackage("com.php25.common.jdbcsample.oracle.model");
        return db;
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
