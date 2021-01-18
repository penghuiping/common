package com.php25.common.jdbcsample.mysql.test.shard;

import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.shard.ShardRuleHashBased;
import com.php25.common.db.core.shard.ShardTableInfo;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Department;
import com.php25.common.jdbcsample.mysql.model.ShardCustomer;
import com.php25.common.jdbcsample.mysql.model.ShardDepartmentRef;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final List<JdbcTemplate> jdbcTemplates = Lists.newArrayList(jdbcTemplate, jdbcTemplate);
    private final List<String> physicalTableNames = Lists.newArrayList("t_customer_0", "t_customer_1");
    private final List<String> physicalTableNames1 = Lists.newArrayList("t_customer_department_0", "t_customer_department_1");

    @Test
    public void query() {
        //like
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).whereLike("username", "jack%").asc("id").select();
        List<ShardCustomer> customers = QueriesExecute.mysql()
                .shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereNotLike("username", "jack%").asc("id").select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        sqlParams = Queries.mysql().from(Department.class).whereEq("name", "it").single();
        Department department = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(sqlParams);
        Assertions.assertThat(department).isNotNull();

        //not eq
        sqlParams = Queries.mysql().from(Department.class).whereNotEq("name", "it").single();
        department = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(sqlParams);
        Assertions.assertThat(department).isNull();

        //between...and..
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereBetween("age", 20, 50).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereNotBetween("age", 20, 50).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereIn("age", Lists.newArrayList(20, 40)).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereGreat("age", 40).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereGreatEq("age", 40).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereLess("age", 0).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereLessEq("age", 0).select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereIsNull("updateTime").select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereIsNotNull("updateTime").select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        sqlParams = Queries.mysql().from(ShardCustomer.class).join(ShardDepartmentRef.class).on("ShardCustomer.id", "ShardDepartmentRef.customerId").select(ShardCustomer.class);
        customers = QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .with(ShardTableInfo.of(ShardDepartmentRef.class, jdbcTemplates, physicalTableNames1))
                .select(sqlParams);

        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(6);

        //alias
        sqlParams = Queries.mysql().from(ShardCustomer.class, "a").join(ShardDepartmentRef.class, "b").on("a.id", "b.customerId").select(ShardCustomer.class);
        customers = QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .with(ShardTableInfo.of(ShardDepartmentRef.class, jdbcTemplates, physicalTableNames1))
                .select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(6);
    }

    @Test
    public void or() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class)
                .where(Queries.mysql().from(ShardCustomer.class).andEq("age", 0).andEq("username", "jack0"))
                .or(Queries.mysql().from(ShardCustomer.class).andEq("age", 0).andEq("username", "mary0")).asc("id")
                .select();
        List<ShardCustomer> customers =
                QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(2);

        SqlParams sqlParams1 = Queries.mysql().from(ShardCustomer.class).whereEq("age", 0).orEq("age", 10).asc("id").select();
        customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams1);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(4);
    }

    @Test
    public void orderBy() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).orderBy("age asc").orderBy("id asc").select();
        List<ShardCustomer> customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        sqlParams = Queries.mysql().from(ShardCustomer.class).asc("age").asc("id").select();
        List<ShardCustomer> customers1 = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);

        this.customers = this.customers.stream().sorted((o1, o2) -> {
            int res = o1.getAge() - o2.getAge();
            if (res != 0) {
                return res;
            }
            return (int) (o1.getId() - o2.getId());
        }).collect(Collectors.toList());

        log.info(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
        for (int i = 0; i < customers.size(); i++) {
            Assertions.assertThat(customers.get(i).getAge()).isEqualTo(this.customers.get(i).getAge());
        }

        Assertions.assertThat(customers1.size()).isEqualTo(this.customers.size());
        for (int i = 0; i < customers1.size(); i++) {
            Assertions.assertThat(customers1.get(i).getAge()).isEqualTo(this.customers.get(i).getAge());
        }
    }


    @Test
    public void groupBy() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).groupBy("age").having("sum_score>100")
                .select(Map.class, "sum(score) as sum_score", "age");
        List<Map> customers1 = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).mapSelect(sqlParams);
        log.info("实际结果为:{}", JsonUtil.toPrettyJson(customers1));
        Assertions.assertThat(customers1).isNotNull();
        Assertions.assertThat(customers1.size()).isEqualTo(3);

//        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(ShardCustomer::getAge, Collectors.averagingInt(ShardCustomer::getScore)));
//        log.info("预期结果为:{}", JsonUtil.toPrettyJson(result));
//
//        for (Map map : customers1) {
//            Assertions.assertThat(BigDecimal.valueOf(result.get(map.get("enable"))).intValue()).isEqualTo(((BigDecimal) map.get("avg_age")).intValue());
//        }
    }

    @Test
    public void findAll() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).select();
        List<ShardCustomer> customers = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
    }

    @Test
    public void findOne() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack0").single();
        ShardCustomer customer = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).single(sqlParams);
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());
    }

    @Test
    public void findOneByHashShardingKey() {
        ShardRule shardRule = new ShardRuleHashBased();
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("id", 1L).single();
        ShardCustomer customer = QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo
                        .of(ShardCustomer.class, jdbcTemplates, physicalTableNames)
                        .shardRule(shardRule, 1L))
                .single(sqlParams);
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());

        sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("id", 2L).single();
        customer = QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo
                        .of(ShardCustomer.class, jdbcTemplates, physicalTableNames)
                        .shardRule(shardRule, 2L))
                .single(sqlParams);
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack1").isEqualTo(customer.getUsername());
    }


    @Test
    public void count() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("enable", "1").count();
        Long count = QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .count(sqlParams);
        Assertions.assertThat(this.customers.stream().filter(a -> a.getEnable() == 1).count()).isEqualTo((long) count);
    }


    @Test
    public void insert() throws Exception {
        ShardRule shardRule = new ShardRuleHashBased();

        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).delete();
        QueriesExecute.mysql()
                .shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .delete(sqlParams);

        ShardCustomer customer = new ShardCustomer();
        customer.setId(1L);
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        sqlParams = Queries.mysql().from(ShardCustomer.class).insert(customer);
        QueriesExecute.mysql()
                .shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)
                        .shardRule(shardRule, customer.getId()))
                .insert(sqlParams);

        ShardCustomer customer1 = new ShardCustomer();
        customer1.setId(2L);
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(LocalDateTime.now());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        sqlParams = Queries.mysql().from(ShardCustomer.class).insert(customer1);
        QueriesExecute.mysql()
                .shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)
                        .shardRule(shardRule, customer1.getId()))
                .insert(sqlParams);

        sqlParams = Queries.mysql().from(ShardCustomer.class).count();
        Assertions.assertThat(2)
                .isEqualTo(QueriesExecute.mysql().shardJdbc()
                        .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                        .count(sqlParams));
    }


    @Test
    public void update() {
        SqlParams sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack0").single();
        ShardCustomer customer = QueriesExecute.mysql()
                .shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .single(sqlParams);

        customer.setUsername("jack-0");
        sqlParams = Queries.mysql().from(ShardCustomer.class).update(customer);
        QueriesExecute.mysql().shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .update(sqlParams);

        sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack0").single();
        customer = QueriesExecute.mysql()
                .shardJdbc()
                .with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .single(sqlParams);
        Assertions.assertThat(customer).isNull();
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack-0").single();
        customer = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames))
                .single(sqlParams);
        Assertions.assertThat(customer).isNotNull();

        ShardCustomer customer1 = new ShardCustomer();
        customer1.setUsername("jack0");
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack-0").update(customer1);
        QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).update(sqlParams);
        sqlParams = Queries.mysql().from(ShardCustomer.class).whereEq("username", "jack0").single();
        customer = QueriesExecute.mysql().shardJdbc().with(ShardTableInfo.of(ShardCustomer.class, jdbcTemplates, physicalTableNames)).single(sqlParams);
        Assertions.assertThat(customer).isNotNull();
    }
}
