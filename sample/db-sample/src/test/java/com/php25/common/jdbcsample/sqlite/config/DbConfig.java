package com.php25.common.jdbcsample.sqlite.config;

import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.DbType;
import com.php25.common.db.EntitiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.sqlite.SQLiteDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Profile(value = "single_db")
@Configuration
public class DbConfig {

    @Bean
    public DataSource druidDataSource() {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:/tmp/test.db");
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

    @Bean
    public DbType dbType() {
        return DbType.SQLITE;
    }


    @PostConstruct
    public void init() {
        new EntitiesScan().scanPackage("com.php25.common.jdbcsample.sqlite.model");
    }


    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }
}
