package com.php25.common;

import com.php25.common.specification.SnowflakeIdWorker;
import com.php25.common.specification.SpringDaoRunner;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * Created by penghuiping on 2018/3/21.
 */
@ComponentScan
public class CommonAutoConfigure {

    @Value("${snowflakeWorkId:0}")
    private Long snowflakeWorkId;

    @Value("${snowflakeDataCenterId:0}")
    private Long snowflakeDataCenterId;


    @Bean
    @ConditionalOnClass({Dao.class})
    SpringDaoRunner springDaoRunner() {
        return new SpringDaoRunner();
    }

    @Bean
    @ConditionalOnClass({Dao.class})
    Dao dao(@Autowired DataSource datasource, @Autowired SpringDaoRunner springDaoRunner) {
        NutDao dao = new NutDao(datasource);
        dao.setRunner(springDaoRunner);
        return dao;
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        return snowflakeIdWorker;
    }
}
