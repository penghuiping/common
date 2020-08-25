package com.php25.timetasks.mysql.config;

import com.php25.timetasks.repository.TimeTaskDbRepository;
import com.php25.timetasks.timewheel.TimeWheel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author penghuiping
 * @date 2020/8/25 13:22
 */
@Configuration
public class TaskConfig  {


    @Bean
    public TimeWheel timeWheel(@Autowired TimeTaskDbRepository timeTaskDbRepository) {
        TimeWheel timeWheel = new TimeWheel();
        timeWheel.setTimeTaskDbRepository(timeTaskDbRepository);
        return timeWheel;
    }
}
