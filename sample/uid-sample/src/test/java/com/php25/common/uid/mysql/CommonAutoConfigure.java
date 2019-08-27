package com.php25.common.uid.mysql;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableJdbcRepositories(basePackages = "com.php25.common.uid.mysql.repository")
@EnableAutoConfiguration
@ComponentScan
public class CommonAutoConfigure {


}
