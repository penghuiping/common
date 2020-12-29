package com.php25.common.jdbcsample.mysql.config;

import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.core.JdbcPair;
import com.php25.common.db.repository.shard.DefaultShardRule;
import com.php25.common.db.repository.shard.ShardRule;
import com.php25.common.db.repository.shard.TwoPhaseCommitTransaction;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author penghuiping
 * @date 2020-12-24
 */
@Profile(value = "many_db")
@Configuration
public class DbConfig1 {
    @Bean
    public DataSource druidDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
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
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    public Db db(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        Db db = new Db(DbType.MYSQL);
        JdbcPair jdbcPair = new JdbcPair(jdbcTemplate, transactionTemplate);
        db.setJdbcPair(jdbcPair);
        db.scanPackage("com.php25.common.jdbcsample.mysql.model");
        return db;
    }

    @Bean
    ShardRule shardRule() {
        return new DefaultShardRule();
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }

    @Bean
    TwoPhaseCommitTransaction twoPhaseCommitTransaction() {
        return new TwoPhaseCommitTransaction();
    }
}
