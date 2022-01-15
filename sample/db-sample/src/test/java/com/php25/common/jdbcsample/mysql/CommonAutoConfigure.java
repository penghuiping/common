package com.php25.common.jdbcsample.mysql;

import com.php25.common.db.core.shard.ShardRuleHashBased;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by penghuiping on 2018/3/21.
 */
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {JdbcRepositoriesAutoConfiguration.class})
@ComponentScan({"com.php25.common.core", "com.php25.common.jdbcsample.mysql"})
public class CommonAutoConfigure {
    @Bean
    ShardRuleHashBased ShardRuleHashBased() {
        return new ShardRuleHashBased();
    }
}
