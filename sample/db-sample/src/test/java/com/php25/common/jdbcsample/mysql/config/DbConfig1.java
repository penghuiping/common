package com.php25.common.jdbcsample.mysql.config;

import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.JdbcPair;
import com.php25.common.db.repository.shard.ShardRule;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Profile(value = "many_db")
@Configuration
public class DbConfig1 {
    @Bean
    @Qualifier("DS0")
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
    @Qualifier("DS1")
    public DataSource druidDataSource1() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariDataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3307/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
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
    @Qualifier("jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Autowired @Qualifier("DS0") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    @Qualifier("jdbcTemplate1")
    public JdbcTemplate jdbcTemplate1(@Autowired @Qualifier("DS1") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Qualifier("tt")
    public TransactionTemplate transactionTemplate(@Qualifier("DS0") DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    @Qualifier("tt1")
    public TransactionTemplate transactionTemplate1(@Qualifier("DS1") DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    @Bean
    public Db db(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate, @Qualifier("tt") TransactionTemplate transactionTemplate) {
        Db db = new Db(DbType.MYSQL);
        JdbcPair jdbcPair = new JdbcPair(jdbcTemplate, transactionTemplate);
        db.setJdbcPair(jdbcPair);
        db.scanPackage("com.php25.common.jdbcsample.mysql.model");
        return db;
    }

    @Bean
    public Db db1(@Qualifier("jdbcTemplate1") JdbcTemplate jdbcTemplate, @Qualifier("tt1") TransactionTemplate transactionTemplate) {
        Db db = new Db(DbType.MYSQL);
        JdbcPair jdbcPair = new JdbcPair(jdbcTemplate, transactionTemplate);
        db.setJdbcPair(jdbcPair);
        db.scanPackage("com.php25.common.jdbcsample.mysql.model");
        return db;
    }

    @Bean
    ShardRule shardRule() {
        return new ShardRule() {
            @Override
            public Db shardPrimaryKey(List<Db> dbs, Object pkValue) {
                if (pkValue instanceof Long || pkValue instanceof Integer) {
                    return dbs.get(Integer.parseInt(pkValue.toString()) % dbs.size());
                } else if (pkValue instanceof String) {
                    char[] values = pkValue.toString().toCharArray();
                    int v = 0;
                    for (char c : values) {
                        v = v + c;
                    }
                    return dbs.get(v % dbs.size());
                }
                return dbs.get(pkValue.hashCode() % dbs.size());
            }
        };
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }
}
