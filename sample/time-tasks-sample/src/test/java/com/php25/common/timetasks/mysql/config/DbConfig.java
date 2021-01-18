package com.php25.common.timetasks.mysql.config;

import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.IdGeneratorImpl;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.DbType;
import com.php25.common.db.EntitiesScan;
import com.php25.common.db.core.JdbcPair;
import com.php25.common.timetasks.repository.TimeTaskDbRepository;
import com.php25.common.timetasks.repository.TimeTaskDbRepositoryImpl;
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
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("root");
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
    public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
        return new TransactionTemplate(platformTransactionManager);
    }

    @Bean
    public EntitiesScan db(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        EntitiesScan db = new EntitiesScan(DbType.MYSQL);
        JdbcPair jdbcPair = new JdbcPair(jdbcTemplate, transactionTemplate);
        db.setJdbcPair(jdbcPair);
        db.scanPackage("com.php25.common.timetasks.model");
        return db;
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }

    @Bean
    public IdGenerator idGenerator() {
        return new IdGeneratorImpl();
    }

    @Bean
    public TimeTaskDbRepository timeTaskDbRepository(EntitiesScan db) {
        return new TimeTaskDbRepositoryImpl(db);
    }
}
