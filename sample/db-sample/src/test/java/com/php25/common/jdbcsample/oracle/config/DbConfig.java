package com.php25.common.jdbcsample.oracle.config;

import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.DbType;
import com.php25.common.db.EntitiesScan;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

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
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    public DbType dbType() {
        return DbType.ORACLE;
    }


    @PostConstruct
    public void init() {
        new EntitiesScan().scanPackage("com.php25.common.jdbcsample.oracle.model");
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }
}
