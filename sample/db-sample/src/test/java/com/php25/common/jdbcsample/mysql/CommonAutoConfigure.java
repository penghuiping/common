package com.php25.common.jdbcsample.mysql;

import com.php25.common.core.service.IdGenerator;
import com.php25.common.core.service.IdGeneratorImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableAutoConfiguration
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    IdGenerator idGeneratorService() {
        return new IdGeneratorImpl();
    }


}
