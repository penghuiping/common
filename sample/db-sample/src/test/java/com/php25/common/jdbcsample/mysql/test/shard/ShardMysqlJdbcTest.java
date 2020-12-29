package com.php25.common.jdbcsample.mysql.test.shard;

import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.ShardCustomer;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/12/24 16:47
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@ActiveProfiles(profiles = {"many_db"})
@RunWith(SpringRunner.class)
public class ShardMysqlJdbcTest extends ShardDbTest {

    private static final Logger log = LoggerFactory.getLogger(ShardMysqlJdbcTest.class);


    @ClassRule
    public static GenericContainer mysql = new GenericContainer<>("mysql:5.7").withExposedPorts(3306);

    static {
        mysql.setPortBindings(Lists.newArrayList("3306:3306"));
        mysql.withEnv("MYSQL_USER", "root");
        mysql.withEnv("MYSQL_ROOT_PASSWORD", "root");
        mysql.withEnv("MYSQL_DATABASE", "test");
    }

    @Test
    public void test() {
        List<ShardCustomer> shardCustomers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).select());
        log.info(JsonUtil.toPrettyJson(shardCustomers));
        Assertions.assertThat(shardCustomers.size()).isEqualTo(6);

        long count = db.getShardSqlExecute().count(db.from(ShardCustomer.class).count());
        Assertions.assertThat(count).isEqualTo(6);

        ShardCustomer shardCustomer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(shardCustomer.getUsername()).isEqualTo("jack0");
    }

}
