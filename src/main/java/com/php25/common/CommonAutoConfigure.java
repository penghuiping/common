package com.php25.common;

import com.php25.common.specification.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

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
    SnowflakeIdWorker snowflakeIdWorker() {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        return snowflakeIdWorker;
    }
}
