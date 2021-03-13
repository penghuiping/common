package com.php25.common.timer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.php25.common.core", "com.php25.common.timer"})
public class CommonAutoConfigure {

    @Bean
    public Timer timer() {
        return new Timer();
    }
}
