package com.php25.common.mongosample;

import com.php25.common.core.util.JsonUtil;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.List;
import java.util.Optional;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest
@ContextConfiguration(classes = {MongoConfiguration.class})
@RunWith(SpringRunner.class)
@EnableMongoRepositories(basePackages = "com.php25.common.mongosample")
public class MongoCommonTest {

    private static final Logger logger = LoggerFactory.getLogger(MongoCommonTest.class);

    @ClassRule
    public static GenericContainer mongo = new GenericContainer<>("mongo").withExposedPorts(27017);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;


    static {
        mongo.setPortBindings(Lists.newArrayList("27017:27017"));
    }

    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void test0() throws Exception {
        User user = new User();
        user.setName("jack");
        user.setAge(12);

        User user1 = new User();
        user1.setName("mary");
        user1.setAge(13);
        mongoTemplate.insertAll(Lists.newArrayList(user, user1));

        List<User> userList = mongoTemplate.findAll(User.class);
        logger.info("所有用户信息:{}", JsonUtil.toPrettyJson(userList));

        User result = mongoTemplate.findOne(Query.query(Criteria.where("name").is("jack")), User.class);
        logger.info("单个用户信息:{}", JsonUtil.toPrettyJson(result));
    }

    @Test
    public void test1() throws Exception {
        User user = new User();
        user.setName("jack");
        user.setAge(12);

        User user1 = new User();
        user1.setName("mary");
        user1.setAge(13);

        userRepository.insert(Lists.newArrayList(user, user1));

        List<User> userList = userRepository.findAll();
        logger.info("所有用户信息:{}", JsonUtil.toPrettyJson(userList));

        Optional<User> userOptional = userRepository.findOne(Example.of(user));
        User result = userOptional.get();
        logger.info("单个用户信息:{}", JsonUtil.toPrettyJson(result));
    }

}


