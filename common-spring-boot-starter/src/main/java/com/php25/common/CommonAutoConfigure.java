package com.php25.common;

import com.php25.common.core.service.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * spring starter自动配置类
 * @author penghuiping
 * @date 2018/3/21
 *
 */
@ComponentScan
public class CommonAutoConfigure {

    @Value("${snowflakeWorkId:0}")
    private Long snowflakeWorkId;

    @Value("${snowflakeDataCenterId:0}")
    private Long snowflakeDataCenterId;


    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        return snowflakeIdWorker;
    }
}
