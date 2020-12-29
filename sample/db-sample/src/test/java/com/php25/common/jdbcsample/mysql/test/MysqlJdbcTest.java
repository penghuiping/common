package com.php25.common.jdbcsample.mysql.test;

import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereLike("username", "jack%").asc("id").select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        customers = db.getBaseSqlExecute().select(db.from(Customer.class)
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
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereBetween("age", 20, 50).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereNotBetween("age", 20, 50).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereIn("age", Lists.newArrayList(20, 40)).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereGreat("age", 40).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereGreatEq("age", 40).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereLess("age", 0).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereLessEq("age", 0).select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereIsNull("updateTime").select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereIsNotNull("updateTime").select());
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        customers = db.getBaseSqlExecute().select(db.from(Customer.class).join(Company.class).on("Customer.companyId", "Company.id").select(Customer.class));
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(6);

        List<Company> companies = db.getBaseSqlExecute().select(db.from(Customer.class).join(Company.class).on("Customer.companyId", "Company.id").whereEq("Company.name", "Google").select(Company.class, "Company.id", "Company.name", "Company.enable", "Company.createTime", "Company.updateTime"));
        System.out.println(JsonUtil.toPrettyJson(companies));
        Assertions.assertThat(companies.size()).isEqualTo(6);

        //alias
        customers = db.getBaseSqlExecute().select(db.from(Customer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").select(Customer.class));
        Assertions.assertThat(customers.size()).isEqualTo(6);
        companies = db.getBaseSqlExecute().select(db.from(Customer.class, "a").join(Company.class, "b").on("a.companyId", "b.id").whereEq("b.name", "Google").select(Company.class, "b.id", "b.name", "b.enable", "b.createTime", "b.updateTime"));
        Assertions.assertThat(companies.size()).isEqualTo(6);


    }

    @Test
    public void or() {
        List<Customer> customers =
                db.getBaseSqlExecute().select(
                        db.from(Customer.class)
                                .where(db.from(Customer.class).andEq("age", 0).andEq("username", "jack0"))
                                .or(db.from(Customer.class).andEq("age", 0).andEq("username", "mary0"))
                                .select());
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(2);

        customers = db.getBaseSqlExecute().select(db.from(Customer.class).whereEq("age", 0).orEq("age", 10).select());
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(3);
    }


    @Test
    public void orderBy() {
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).orderBy("age asc").select());
        List<Customer> customers1 = db.getBaseSqlExecute().select(db.from(Customer.class).asc("age").select());
        Assertions.assertThat(customers.size()).isEqualTo(customers1.size());
        for (int i = 0; i < customers.size(); i++) {
            Assertions.assertThat(customers.get(i).getAge()).isEqualTo(customers1.get(i).getAge());
        }
    }

    @Test
    public void groupBy() {
        List<Map> customers1 = db.getBaseSqlExecute().mapSelect(db.from(Customer.class).groupBy("enable").having("avg_age>1").select(Map.class, "avg(age) as avg_age", "enable"));
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
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
    }

    @Test
    public void findOne() {
        Customer customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());
    }

    @Test
    public void count() {
        Long count = db.getBaseSqlExecute().count(db.from(Customer.class).whereEq("enable", "1").count());
        Assertions.assertThat(this.customers.stream().filter(a -> a.getEnable() == 1).count()).isEqualTo((long) count);
    }

    @Test
    public void insert() throws Exception {
        db.getBaseSqlExecute().delete(db.from(Company.class).delete());
        db.getBaseSqlExecute().delete(db.from(Customer.class).delete());

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
        db.getBaseSqlExecute().insert(db.from(Customer.class).insert(customer));

        Customer customer1 = new Customer();
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(LocalDateTime.now());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());
        db.getBaseSqlExecute().insert(db.from(Customer.class).insert(customer1));

        company.setCustomers(Lists.newArrayList(customer, customer1));
        db.getBaseSqlExecute().insert(db.from(Company.class).insert(company));
        Assertions.assertThat(2).isEqualTo(db.getBaseSqlExecute().count(db.from(Customer.class).count()));
        Assertions.assertThat(1).isEqualTo(db.getBaseSqlExecute().count(db.from(Company.class).count()));
    }

    @Test
    public void batchInsert() throws Exception {
        db.getBaseSqlExecute().delete(db.from(Company.class).delete());
        db.getBaseSqlExecute().delete(db.from(Customer.class).delete());

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

        db.getBaseSqlExecute().insertBatch(db.from(Company.class).insertBatch(Lists.newArrayList(company)));
        db.getBaseSqlExecute().insertBatch(db.from(Customer.class).insertBatch(Lists.newArrayList(customer, customer1)));

        Assertions.assertThat(2).isEqualTo(db.getBaseSqlExecute().count(db.from(Customer.class).count()));
        Assertions.assertThat(1).isEqualTo(db.getBaseSqlExecute().count(db.from(Company.class).count()));
    }

    @Test
    public void update() {
        Customer customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack0").single());
        customer.setUsername("jack-0");
        db.getBaseSqlExecute().update(db.from(Customer.class).update(customer));
        customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNull();
        customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack-0").single());
        Assertions.assertThat(customer).isNotNull();


        Customer customer1 = new Customer();
        customer1.setUsername("jack0");
        db.getBaseSqlExecute().update(db.from(Customer.class).whereEq("username", "jack-0").update(customer1));
        customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customer).isNotNull();
    }

    @Test
    public void batchUpdate() {
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).select());
        customers = customers.stream().map(a -> {
            a.setUsername(a.getUsername().replace("jack", "tom"));
            return a;
        }).collect(Collectors.toList());
        int[] arr = db.getBaseSqlExecute().updateBatch(db.from(Customer.class).updateBatch(customers));
        for (int e : arr) {
            Assertions.assertThat(e).isEqualTo(1);
        }
    }


    @Test
    public void updateVersion() throws Exception {
        CountDownLatch countDownLatch1 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        AtomicInteger atomicInteger = new AtomicInteger();
        List<Callable<Object>> runnables = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            runnables.add(() -> {
                Customer customer = db.getBaseSqlExecute().single(
                        db.from(Customer.class).whereEq("username", "jack0").single());
                customer.setScore(customer.getScore().subtract(BigDecimal.valueOf(1)));
                int rows = db.getBaseSqlExecute().update(db.from(Customer.class).update(customer));
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
        Customer customer = db.getBaseSqlExecute().single(db.from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(1000L - customer.getScore().longValue()).isEqualTo(atomicInteger.get());
        Assertions.assertThat(customer.getVersion()).isEqualTo(atomicInteger.get());
    }

    @Test
    public void delete() {
        db.getBaseSqlExecute().delete(db.from(Customer.class).whereLike("username", "jack%").delete());
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }

    @Test
    public void deleteAlias() {
        db.getBaseSqlExecute().delete(db.from(Customer.class, "a").whereLike("a.username", "jack%").delete());
        List<Customer> customers = db.getBaseSqlExecute().select(db.from(Customer.class).select());
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }
}
