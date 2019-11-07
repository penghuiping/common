package com.php25.common.jdbcsample.oracle.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent;
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

//    @Bean
//    public DataSource druidDataSource() {
//        DruidDataSource druidDataSource = new DruidDataSource();
//        druidDataSource.setDriverClassName("org.h2.Driver");
//        druidDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=ORACLE;");
//        druidDataSource.setUsername("");
//        druidDataSource.setPassword("");
//        druidDataSource.setMaxActive(15);
//        druidDataSource.setTestWhileIdle(false);
//        try {
//            druidDataSource.setFilters("stat, wall");
//        } catch (SQLException e) {
//            LoggerFactory.getLogger(com.php25.common.jdbcsample.mysql.config.DbConfig.class).error("出错啦！", e);
//        }
//        return druidDataSource;
//    }

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        druidDataSource.setUrl("jdbc:oracle:thin:@//121.36.159.211:1521/helowin");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(15);
        druidDataSource.setMinIdle(1);
        druidDataSource.setTestWhileIdle(false);
//        try {
//            druidDataSource.setFilters("stat, wall");
//        } catch (SQLException e) {
//            LoggerFactory.getLogger(com.php25.common.jdbcsample.mysql.config.DbConfig.class).error("出错啦！", e);
//        }
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

    @Bean
    public ApplicationListener<BeforeSaveEvent> timeStampingSaveTime(@Autowired UidGenerator uidGenerator) {
        return event -> {
            Object entity = event.getEntity();
            if (entity instanceof Customer) {
                if (null == ((Customer) entity).getId()) {
                    Customer customer = (Customer) entity;
                    customer.setId(uidGenerator.getUID());
                }
            } else if (entity instanceof Company) {
                if (null == ((Company) entity).getId()) {
                    Company company = (Company) entity;
                    company.setId(uidGenerator.getUID());
                }
            }
        };
    }
}