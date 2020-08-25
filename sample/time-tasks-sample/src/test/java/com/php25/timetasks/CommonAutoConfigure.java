package com.php25.timetasks;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableAutoConfiguration(exclude = {JdbcRepositoriesAutoConfiguration.class})
@ComponentScan
public class CommonAutoConfigure {

}
