package com.php25.common.jdbcsample.mysql.test;

import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@ActiveProfiles(profiles = {"single_db"})
@RunWith(SpringRunner.class)
public class MysqlJdbcTest extends DbTest {

    private final static Logger log = LoggerFactory.getLogger(MysqlJdbcTest.class);

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
        SqlParams sqlParams = Queries.mysql().from(Customer.class).whereLike("username", "jack%").asc("id").select();
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        sqlParams = Queries.mysql().from(Customer.class).whereNotLike("username", "jack%").asc("id").select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        sqlParams = Queries.mysql().from(Company.class).whereEq("name", "Google").single();
        Company company = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(sqlParams);
        Assertions.assertThat(company).isNotNull();

        //not eq
        sqlParams = Queries.mysql().from(Company.class).whereNotEq("name", "Google").single();
        company = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(sqlParams);
        Assertions.assertThat(company).isNull();

        //between...and..
        sqlParams = Queries.mysql().from(Customer.class).whereBetween("age", 20, 50).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        sqlParams = Queries.mysql().from(Customer.class).whereNotBetween("age", 20, 50).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        sqlParams = Queries.mysql().from(Customer.class).whereIn("age", Lists.newArrayList(20, 40)).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        sqlParams = Queries.mysql().from(Customer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        sqlParams = Queries.mysql().from(Customer.class).whereGreat("age", 40).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        sqlParams = Queries.mysql().from(Customer.class).whereGreatEq("age", 40).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        sqlParams = Queries.mysql().from(Customer.class).whereLess("age", 0).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        sqlParams = Queries.mysql().from(Customer.class).whereLessEq("age", 0).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        sqlParams = Queries.mysql().from(Customer.class).whereIsNull("updateTime").select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        sqlParams = Queries.mysql().from(Customer.class).whereIsNotNull("updateTime").select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        sqlParams = Queries.mysql().from(Customer.class).join(Company.class).on("Customer.companyId", "Company.id").select(Customer.class);
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(6);

        sqlParams = Queries.mysql().from(Customer.class).join(Company.class).on("Customer.companyId", "Company.id").whereEq("Company.name", "Google").select(Company.class, "Company.id", "Company.name", "Company.enable", "Company.createTime", "Company.updateTime");
        List<Company> companies = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        System.out.println(JsonUtil.toPrettyJson(companies));
        Assertions.assertThat(companies.size()).isEqualTo(6);

        //alias
        sqlParams = Queries.mysql().from(Customer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").select(Customer.class);
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers.size()).isEqualTo(6);
        sqlParams = Queries.mysql().from(Customer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").whereEq("b.name", "Google").select(Company.class, "b.id", "b.name", "b.enable", "b.createTime", "b.updateTime");
        companies = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(companies.size()).isEqualTo(6);
    }

    @Test
    public void limit() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).asc("id").limit(2, 2).select();
        List<Customer> result = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        log.info(JsonUtil.toPrettyJson(result));
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(3);
        Assertions.assertThat(result.get(1).getId()).isEqualTo(4);
    }

    @Test
    public void or() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class, "a")
                .where(Queries.group().andEq("a.age", 0).andEq("a.username", "jack0"))
                .or(Queries.group().andEq("a.age", 0).andEq("a.username", "mary0"))
                .select();
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(2);

        sqlParams = Queries.mysql().from(Customer.class).whereEq("age", 0).orEq("age", 10).select();
        customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(3);
    }


    @Test
    public void orderBy() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).orderBy("age asc").select();
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        SqlParams sqlParams1 = Queries.mysql().from(Customer.class).asc("age").select();
        List<Customer> customers1 = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams1);
        Assertions.assertThat(customers.size()).isEqualTo(customers1.size());
        for (int i = 0; i < customers.size(); i++) {
            Assertions.assertThat(customers.get(i).getAge()).isEqualTo(customers1.get(i).getAge());
        }
    }

    @Test
    public void groupBy() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).groupBy("enable").having("avg_age>1").select(Map.class, "avg(age) as avg_age", "enable");
        List<Map> customers1 = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).mapSelect(sqlParams);
        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(Customer::getEnable, Collectors.averagingInt(Customer::getAge)));
        System.out.println(JsonUtil.toPrettyJson(result));
        Assertions.assertThat(customers1).isNotNull();
        Assertions.assertThat(customers1.size() > 0);
        for (Map map : customers1) {
            Assertions.assertThat(BigDecimal.valueOf(result.get(map.get("enable"))).intValue()).isEqualTo(((BigDecimal) map.get("avg_age")).intValue());
        }
    }

    @Test
    public void findAll() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).select();
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(sqlParams);
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
    }

    @Test
    public void findOne() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).whereEq("username", "jack0").single();
        Customer customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(sqlParams);
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());
    }

    @Test
    public void count() {
        SqlParams sqlParams = Queries.mysql().from(Customer.class).whereEq("enable", 1).count();
        Long count = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).count(sqlParams);
        Assertions.assertThat(this.customers.stream().filter(a -> a.getEnable() == 1).count()).isEqualTo((long) count);
    }

    @Test
    public void insert() throws Exception {
        SqlParams sqlParams = Queries.mysql().from(Company.class).delete();
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(sqlParams);

        SqlParams sqlParams1 = Queries.mysql().from(Customer.class).delete();
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(sqlParams1);

        Company company = new Company();
        company.setName("test");
        company.setId(snowflakeIdWorker.nextId());
        company.setCreateTime(new Date());
        company.setEnable(1);

        Customer customer = new Customer();
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        customer.setCompanyId(company.getId());
        sqlParams = Queries.mysql().from(Customer.class).insert(customer);
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams);

        Customer customer1 = new Customer();
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(LocalDateTime.now());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());
        sqlParams = Queries.mysql().from(Customer.class).insert(customer1);
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams);

        company.setCustomers(Lists.newArrayList(customer, customer1));
        sqlParams = Queries.mysql().from(Company.class).insert(company);
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams);
        Assertions.assertThat(QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).count(Queries.mysql().from(Customer.class).count())).isEqualTo(2);
        Assertions.assertThat(QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).count(Queries.mysql().from(Company.class).count())).isEqualTo(1);
    }

    @Test
    public void batchInsert() throws Exception {
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(Queries.mysql().from(Company.class).delete());
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(Queries.mysql().from(Customer.class).delete());

        Company company = new Company();
        company.setName("test");
        company.setId(snowflakeIdWorker.nextId());
        company.setCreateTime(new Date());
        company.setEnable(1);

        Customer customer = new Customer();
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setStartTime(LocalDateTime.now());
        customer.setEnable(1);
        customer.setCompanyId(company.getId());

        Customer customer1 = new Customer();
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setStartTime(LocalDateTime.now());
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());

        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insertBatch(Queries.mysql().from(Company.class).insertBatch(Lists.newArrayList(company)));
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insertBatch(Queries.mysql().from(Customer.class).insertBatch(Lists.newArrayList(customer, customer1)));

        Assertions.assertThat(2).isEqualTo(QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).count(Queries.mysql().from(Customer.class).count()));
        Assertions.assertThat(1).isEqualTo(QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).count(Queries.mysql().from(Company.class).count()));
    }

    @Test
    public void update() {
        Customer customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(Queries.mysql().from(Customer.class).whereEq("username", "jack0").single());
        customer.setUsername("jack-0");
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).update(Queries.mysql().from(Customer.class).update(customer));
        customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(Queries.mysql().from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNull();
        customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(Queries.mysql().from(Customer.class).whereEq("username", "jack-0").single());
        Assertions.assertThat(customer).isNotNull();

        Customer customer1 = new Customer();
        customer1.setUsername("jack0");
        SqlParams sqlParams = Queries.mysql().from(Customer.class).whereEq("username", "jack-0")
                .update(customer1);
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).update(sqlParams);
        customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(Queries.mysql().from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNotNull();
    }

    @Test
    public void batchUpdate() {
        List<Customer> customers1 = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(Queries.mysql().from(Customer.class).select());
        customers1 = customers1.stream().map(a -> {
            a.setUsername(a.getUsername().replace("jack", "tom"));
            return a;
        }).collect(Collectors.toList());

        SqlParams sqlParams1 = Queries.mysql().from(Customer.class).updateBatch(customers1);
        int[] arr = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).updateBatch(sqlParams1);

        SqlParams sqlParams2 = Queries.mysql().from(Customer.class).whereLike("username", "tom%").select();
        List<Customer> customers2 = QueriesExecute.mysql().singleJdbc()
                .with(jdbcTemplate).select(sqlParams2);
        Assertions.assertThat(customers2.size()).isEqualTo(3);
    }


    @Test
    public void updateVersion() throws Exception {
        CountDownLatch countDownLatch1 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        AtomicInteger atomicInteger = new AtomicInteger();
        List<Callable<Object>> runnables = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            runnables.add(() -> {
                Customer customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(
                        Queries.mysql().from(Customer.class).whereEq("username", "jack0").single());
                customer.setScore(customer.getScore().subtract(BigDecimal.valueOf(1)));
                int rows = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).update(Queries.mysql().from(Customer.class).update(customer));
                if (rows > 0) {
                    atomicInteger.addAndGet(1);
                }
                countDownLatch1.countDown();
                return true;
            });
        }
        executorService.invokeAll(runnables);

        countDownLatch1.await();
        System.out.println("===========>更新成功的数量:" + atomicInteger.get());
        Customer customer = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).single(Queries.mysql().from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(1000L - customer.getScore().longValue()).isEqualTo(atomicInteger.get());
        Assertions.assertThat(customer.getVersion()).isEqualTo(atomicInteger.get());
    }

    @Test
    public void delete() {
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(Queries.mysql().from(Customer.class).whereLike("username", "jack%").delete());
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(Queries.mysql().from(Customer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }

    @Test
    public void deleteAlias() {
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).delete(Queries.mysql().from(Customer.class, "a").whereLike("a.username", "jack%").delete());
        List<Customer> customers = QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).select(Queries.mysql().from(Customer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }
}
