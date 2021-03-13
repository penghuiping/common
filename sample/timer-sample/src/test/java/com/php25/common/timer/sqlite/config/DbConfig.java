package com.php25.common.timer.sqlite.config;

import com.php25.common.db.DbType;
import com.php25.common.db.EntitiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.sqlite.SQLiteDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;


@Configuration
public class DbConfig {
    @Bean
    public DataSource druidDataSource() {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:test.db");
        return sqLiteDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @PostConstruct
    public void init() {
        EntitiesScan db = new EntitiesScan();
        db.scanPackage("com.php25.common.timer.model");
    }

    @Bean
    public DbType dbType() {
        return DbType.SQLITE;
    }
}
