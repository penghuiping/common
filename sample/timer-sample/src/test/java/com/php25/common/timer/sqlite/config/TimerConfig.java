package com.php25.common.timer.sqlite.config;

import com.php25.common.db.DbType;
import com.php25.common.mq.MessageQueueManager;
import com.php25.common.mq.redis.RedisMessageQueueManager;
import com.php25.common.redis.RedisManager;
import com.php25.common.redis.local.LocalRedisManager;
import com.php25.common.timer.Timer;
import com.php25.common.timer.manager.JobManager;
import com.php25.common.timer.manager.RedisJobManager;
import com.php25.common.timer.repository.JobModelRepository;
import com.php25.common.timer.repository.JobModelRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author penghuiping
 * @date 2021/3/13 12:29
 */
@Configuration
public class TimerConfig {

    @Bean
    JobModelRepository jobModelRepository(DbType dbType, JdbcTemplate jdbcTemplate) {
        return new JobModelRepositoryImpl(dbType, jdbcTemplate);
    }

    @Bean
    JobManager jobManager(JobModelRepository jobModelRepository
            , MessageQueueManager messageQueueManager
            , RedisManager redisManager
            , ExecutorService pool) {
        return new RedisJobManager(jobModelRepository, messageQueueManager, redisManager, pool);
    }

    @Bean
    RedisManager redisManager() {
        return new LocalRedisManager(1024);
    }

    @Bean
    MessageQueueManager messageQueueManager(RedisManager redisManager) {
        return new RedisMessageQueueManager(redisManager);
    }

    @Bean
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    Timer timer(JobManager jobManager) {
        return new Timer(jobManager);
    }
}
