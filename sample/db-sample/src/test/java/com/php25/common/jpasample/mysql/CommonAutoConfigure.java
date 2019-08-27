package com.php25.common.jpasample.mysql;

import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.IdGeneratorServiceImpl;
import com.php25.common.core.service.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by penghuiping on 2018/3/21.
 */
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    IdGeneratorService idGeneratorService() {
        return new IdGeneratorServiceImpl();
    }
}
