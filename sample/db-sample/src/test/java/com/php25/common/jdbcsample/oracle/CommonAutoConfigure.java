package com.php25.common.jdbcsample.oracle;

import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.IdGeneratorImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableAutoConfiguration(exclude = {JdbcRepositoriesAutoConfiguration.class})
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    IdGenerator idGeneratorService() {
        return new IdGeneratorImpl();
    }

}
