package com.php25.common.jdbcsample.postgres;

import com.php25.common.core.service.IdGenerator;
import com.php25.common.core.service.IdGeneratorImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableJdbcRepositories(basePackages = "com.php25.common.jdbcsample.postgres.repository")
@EnableAutoConfiguration
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    IdGenerator idGeneratorService() {
        return new IdGeneratorImpl();
    }

}
