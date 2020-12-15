package com.php25.common.jdbcsample.oracle.config;

import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.core.JdbcPair;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Configuration
public class DbConfig {

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
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    Db db(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        Db db = new Db(DbType.ORACLE);
        JdbcPair jdbcPair = new JdbcPair(jdbcTemplate, transactionTemplate);
        db.setJdbcPair(jdbcPair);
        db.scanPackage("com.php25.common.jdbcsample.oracle.model");
        return db;
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }
}
