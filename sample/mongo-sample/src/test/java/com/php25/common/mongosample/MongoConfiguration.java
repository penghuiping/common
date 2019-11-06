package com.php25.common.mongosample;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author penghuiping
 * @date 2019/11/6 13:29
 */

public class MongoConfiguration {

    @Bean
    public SimpleMongoDbFactory simpleMongoDbFactory() {
        return new SimpleMongoDbFactory(new MongoClient("localhost", 27017), "test");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(simpleMongoDbFactory());
    }

}
