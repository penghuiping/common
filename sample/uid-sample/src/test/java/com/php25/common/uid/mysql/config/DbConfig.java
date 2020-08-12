package com.php25.common.uid.mysql.config;

import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.worker.DisposableWorkerIdAssigner;
import com.baidu.fsg.uid.worker.WorkerIdAssigner;
import com.baidu.fsg.uid.worker.dao.WorkerNodeDAO;
import com.baidu.fsg.uid.worker.dao.WorkerNodeDaoImpl;
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
public class DbConfig {

    @Bean
    public DataSource druidDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("org.h2.Driver");
        hikariDataSource.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;");
        hikariDataSource.setUsername("");
        hikariDataSource.setPassword("");
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
        Db db = new Db(DbType.MYSQL);
        db.setJdbcOperations(jdbcTemplate);
        return db;
    }


    @Bean
    UidGenerator uidGenerator(WorkerIdAssigner workerIdAssigner) {
        DefaultUidGenerator defaultUidGenerator = new DefaultUidGenerator();
        defaultUidGenerator.setTimeBits(29);
        defaultUidGenerator.setWorkerBits(21);
        defaultUidGenerator.setSeqBits(13);
        defaultUidGenerator.setWorkerIdAssigner(workerIdAssigner);
        defaultUidGenerator.setEpochStr("2019-08-01");
        return defaultUidGenerator;
    }

    @Bean
    DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }

    @Bean
    WorkerNodeDAO workerNodeDAO(Db db) {
        return new WorkerNodeDaoImpl(db);
    }

}
