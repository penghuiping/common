package com.php25.common.jdbcsample.mysql;

import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.IdGeneratorServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableJdbcRepositories(basePackages = "com.php25.common.jdbcsample.mysql.repository")
@EnableAutoConfiguration
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    IdGeneratorService idGeneratorService() {
        return new IdGeneratorServiceImpl();
    }


}
