package com.php25.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.repository.impl.BaseRepositoryImpl;
import com.php25.common.service.IdGeneratorService;
import com.php25.common.service.RedisService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest
@DataJpaTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@EntityScan(basePackages = {"com.php25"})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class RedisCommonTest {

    private static final Logger logger = LoggerFactory.getLogger(RedisCommonTest.class);


    @Autowired
    IdGeneratorService idGeneratorService;

    @Qualifier("redisServiceSpring")
    @Autowired
    RedisService redisService;

    @Autowired
    ObjectMapper objectMapper;


    @Before
    public void test() throws Exception {
        redisService.remove("test");
        for (int i = 0; i < 100; i++) {
            Long result = redisService.incr("test");
            System.out.println(result);
        }
    }


    @Test
    public void idGeneratorService() throws Exception {
        logger.info("snowflake:" + idGeneratorService.getModelPrimaryKeyNumber());
        logger.info("uuid:" + idGeneratorService.getModelPrimaryKey());
    }

}
