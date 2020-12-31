package com.php25.common.jdbcsample.mysql.test.shard;

import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Company;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Test
    public void query() {
        //like
        List<ShardCustomer> customers = db.getShardSqlExecute()
                .select(db.from(ShardCustomer.class).whereLike("username", "jack%").asc("id").select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class)
                .whereNotLike("username", "jack%").asc("id").select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        Company company = db.getBaseSqlExecute().single(db.from(Company.class).whereEq("name", "Google").single());
        Assertions.assertThat(company).isNotNull();

        //not eq
        company = db.getBaseSqlExecute().single(db.from(Company.class).whereNotEq("name", "Google").single());
        Assertions.assertThat(company).isNull();

        //between...and..
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereBetween("age", 20, 50).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereNotBetween("age", 20, 50).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereIn("age", Lists.newArrayList(20, 40)).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereGreat("age", 40).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereGreatEq("age", 40).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereLess("age", 0).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereLessEq("age", 0).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereIsNull("updateTime").select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereIsNotNull("updateTime").select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).join(Company.class).on("ShardCustomer.companyId", "Company.id").select(ShardCustomer.class));
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(6);

        List<Company> companies = db.getShardSqlExecute().select(db.from(ShardCustomer.class).join(Company.class).on("ShardCustomer.companyId", "Company.id").whereEq("Company.name", "Google").select(Company.class, "Company.id", "Company.name", "Company.enable", "Company.createTime", "Company.updateTime"));
        System.out.println(JsonUtil.toPrettyJson(companies));
        Assertions.assertThat(companies.size()).isEqualTo(6);

        //alias
        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").select(ShardCustomer.class));
        Assertions.assertThat(customers.size()).isEqualTo(6);
        companies = db.getShardSqlExecute().select(db.from(ShardCustomer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").whereEq("b.name", "Google").select(Company.class, "b.id", "b.name", "b.enable", "b.createTime", "b.updateTime"));
        Assertions.assertThat(companies.size()).isEqualTo(6);
    }

    @Test
    public void or() {
        List<ShardCustomer> customers =
                db.getShardSqlExecute().select(
                        db.from(ShardCustomer.class)
                                .where(db.from(ShardCustomer.class).andEq("age", 0).andEq("username", "jack0"))
                                .or(db.from(ShardCustomer.class).andEq("age", 0).andEq("username", "mary0"))
                                .select());
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(2);

        customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).whereEq("age", 0).orEq("age", 10).select());
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(3);
    }

    @Test
    public void orderBy() {
        List<ShardCustomer> customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).orderBy("age asc").select());
        List<ShardCustomer> customers1 = db.getShardSqlExecute().select(db.from(ShardCustomer.class).asc("age").select());
        Assertions.assertThat(customers.size()).isEqualTo(customers1.size());
        for (int i = 0; i < customers.size(); i++) {
            Assertions.assertThat(customers.get(i).getAge()).isEqualTo(customers1.get(i).getAge());
        }
    }

    @Test
    public void groupBy() {
        List<Map> customers1 = db.getShardSqlExecute().mapSelect(db.from(ShardCustomer.class).groupBy("enable").having("avg_age>1").select(Map.class, "avg(age) as avg_age", "enable"));
        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(ShardCustomer::getEnable, Collectors.averagingInt(ShardCustomer::getAge)));
        System.out.println(JsonUtil.toPrettyJson(result));
        Assertions.assertThat(customers1).isNotNull();
        Assertions.assertThat(customers1.size() > 0);
        for (Map map : customers1) {
            Assertions.assertThat(BigDecimal.valueOf(result.get(map.get("enable"))).intValue()).isEqualTo(((BigDecimal) map.get("avg_age")).intValue());
        }
    }

    @Test
    public void findAll() {
        List<ShardCustomer> customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
    }

    @Test
    public void findOne() {
        ShardCustomer customer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());
    }


    @Test
    public void count() {
        Long count = db.getShardSqlExecute().count(db.from(ShardCustomer.class).whereEq("enable", "1").count());
        Assertions.assertThat(this.customers.stream().filter(a -> a.getEnable() == 1).count()).isEqualTo((long) count);
    }

    @Test
    public void insert() throws Exception {
        db.getBaseSqlExecute().delete(db.from(Company.class).delete());
        db.getShardSqlExecute().delete(db.from(ShardCustomer.class).delete());

        Company company = new Company();
        company.setName("test");
        company.setId(snowflakeIdWorker.nextId());
        company.setCreateTime(new Date());
        company.setEnable(1);


        ShardCustomer customer = new ShardCustomer();
        customer.setId(1L);
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        customer.setCompanyId(company.getId());
        db.getShardSqlExecute().insert(db.from(ShardCustomer.class).insert(customer));

        ShardCustomer customer1 = new ShardCustomer();
        customer1.setId(2L);
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(LocalDateTime.now());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());
        db.getShardSqlExecute().insert(db.from(ShardCustomer.class).insert(customer1));

        db.getBaseSqlExecute().insert(db.from(Company.class).insert(company));
        Assertions.assertThat(2).isEqualTo(db.getShardSqlExecute().count(db.from(ShardCustomer.class).count()));
        Assertions.assertThat(1).isEqualTo(db.getBaseSqlExecute().count(db.from(Company.class).count()));
    }

    @Test
    public void batchInsert() throws Exception {
        db.getBaseSqlExecute().delete(db.from(Company.class).delete());
        db.getShardSqlExecute().delete(db.from(ShardCustomer.class).delete());

        Company company = new Company();
        company.setName("test");
        company.setId(snowflakeIdWorker.nextId());
        company.setCreateTime(new Date());
        company.setEnable(1);

        ShardCustomer customer = new ShardCustomer();
        customer.setId(1L);
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setStartTime(LocalDateTime.now());
        customer.setEnable(1);
        customer.setCompanyId(company.getId());

        ShardCustomer customer1 = new ShardCustomer();
        customer1.setId(2L);
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setStartTime(LocalDateTime.now());
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());

        db.getBaseSqlExecute().insertBatch(db.from(Company.class).insertBatch(Lists.newArrayList(company)));
        db.getShardSqlExecute().insertBatch(db.from(ShardCustomer.class).insertBatch(Lists.newArrayList(customer, customer1)));

        Assertions.assertThat(2).isEqualTo(db.getShardSqlExecute().count(db.from(ShardCustomer.class).count()));
        Assertions.assertThat(1).isEqualTo(db.getBaseSqlExecute().count(db.from(Company.class).count()));
    }


    @Test
    public void update() {
        ShardCustomer customer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack0").single());
        customer.setUsername("jack-0");
        db.getShardSqlExecute().update(db.from(ShardCustomer.class).update(customer));
        customer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNull();
        customer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack-0").single());
        Assertions.assertThat(customer).isNotNull();


        ShardCustomer customer1 = new ShardCustomer();
        customer1.setUsername("jack0");
        db.getShardSqlExecute().update(db.from(ShardCustomer.class).whereEq("username", "jack-0").update(customer1));
        customer = db.getShardSqlExecute().single(db.from(ShardCustomer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNotNull();
    }

    @Test
    public void batchUpdate() {
        List<ShardCustomer> customers = db.getShardSqlExecute().select(db.from(ShardCustomer.class).select());
        customers = customers.stream().map(a -> {
            a.setUsername(a.getUsername().replace("jack", "tom"));
            return a;
        }).collect(Collectors.toList());
        int[] arr = db.getShardSqlExecute().updateBatch(db.from(ShardCustomer.class).updateBatch(customers));

        long count = db.getShardSqlExecute().count(db.from(ShardCustomer.class).whereLike("username", "tom%").count());
        Assertions.assertThat(count).isEqualTo(3);
    }

}
