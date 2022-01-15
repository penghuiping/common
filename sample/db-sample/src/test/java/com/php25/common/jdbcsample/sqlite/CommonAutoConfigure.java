package com.php25.common.jdbcsample.sqlite;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration(exclude = {JdbcRepositoriesAutoConfiguration.class})
@ComponentScan
public class CommonAutoConfigure {


}
