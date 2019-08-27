package com.php25.common.jdbcsample.oracle.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.oracle.CommonAutoConfigure;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
public class OracleJdbcTest extends DbTest {

    private Logger log = LoggerFactory.getLogger(OracleJdbcTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    protected void initDb() {
        this.db = new Db(jdbcTemplate, DbType.ORACLE);
    }

    @Test
    public void query() {
        //like
        List<Customer> customers = db.cndJdbc(Customer.class)
                .whereLike("username", "jack%").asc("id").select();
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        customers = db.cndJdbc(Customer.class)
                .whereNotLike("username", "jack%").asc("id").select();
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        Company company = db.cndJdbc(Company.class).whereEq("name", "Google").single();
        Assert.assertNotNull(company);

        //not eq
        company = db.cndJdbc(Company.class).whereNotEq("name", "Google").single();
        Assert.assertNull(company);

        //between...and..
        customers = db.cndJdbc(Customer.class).whereBetween("age", 20, 50).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        customers = db.cndJdbc(Customer.class).whereNotBetween("age", 20, 50).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        customers = db.cndJdbc(Customer.class).whereIn("age", Lists.newArrayList(20, 40)).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        customers = db.cndJdbc(Customer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        customers = db.cndJdbc(Customer.class).whereGreat("age", 40).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        customers = db.cndJdbc(Customer.class).whereGreatEq("age", 40).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        customers = db.cndJdbc(Customer.class).whereLess("age", 0).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        customers = db.cndJdbc(Customer.class).whereLessEq("age", 0).select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        customers = db.cndJdbc(Customer.class).whereIsNull("updateTime").select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        customers = db.cndJdbc(Customer.class).whereIsNotNull("updateTime").select();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        customers = db.cndJdbc(Customer.class).join(Company.class, "companyId").select(Customer.class);
        System.out.println(JsonUtil.toPrettyJson(customers));

        List<Company> companies = db.cndJdbc(Customer.class).join(Company.class, "companyId").whereEq(Customer.class, "id", 1).select(Company.class);
        System.out.println(JsonUtil.toPrettyJson(companies));
    }


    @Test
    public void orderBy() {
        List<Customer> customers = db.cndJdbc(Customer.class).orderBy("age asc").select();
        List<Customer> customers1 = db.cndJdbc(Customer.class).asc("age").select();
        Assert.assertEquals(customers.size(), customers1.size());
        for (int i = 0; i < customers.size(); i++) {
            Assert.assertEquals(customers.get(i).getAge(), customers1.get(i).getAge());
        }
    }

    @Test
    public void groupBy() {
        CndJdbc cndJdbc = db.cndJdbc(Customer.class);
        List<Map> customers1 = cndJdbc.groupBy("enable").mapSelect("avg(age) as avg_age", "enable");
        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(Customer::getEnable, Collectors.averagingInt(Customer::getAge)));
        System.out.println(JsonUtil.toPrettyJson(result));
        Assert.assertTrue(null != customers1 && customers1.size() > 0);
        for (Map map : customers1) {
            Integer key1 = Integer.parseInt(map.get("enable".toUpperCase()).toString());
            Integer key2 = Integer.parseInt(map.get("avg_age".toUpperCase()).toString());
            Assert.assertTrue(BigDecimal.valueOf(result.get(key1)).intValue() == BigDecimal.valueOf(key2).intValue());
        }
    }

    @Test
    public void findAll() {
        List<Customer> customers = db.cndJdbc(Customer.class).select();
        Assert.assertNotNull(customers);
        Assert.assertEquals(customers.size(), this.customers.size());
    }

    @Test
    public void findOne() {
        Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assert.assertTrue(null != customer && "jack0".equals(customer.getUsername()));
    }

    @Test
    public void count() {
        Long count = db.cndJdbc(Customer.class).whereEq("enable", "1").count();
        Assert.assertEquals(this.customers.stream().filter(a -> a.getEnable() == 1).count(), (long) count);
    }

    @Test
    public void insert() throws Exception {
        db.cndJdbc(Company.class).delete();
        db.cndJdbc(Customer.class).delete();

        Company company = new Company();
        company.setName("test");
        company.setId(uidGenerator.getUID());
        company.setCreateTime(new Date());
        company.setEnable(1);


        Customer customer = new Customer();
        if (!isSequence)
            customer.setId(uidGenerator.getUID());
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        customer.setCompanyId(company.getId());
        db.cndJdbc(Customer.class).insert(customer);

        Customer customer1 = new Customer();
        if (!isSequence)
            customer1.setId(uidGenerator.getUID());
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(LocalDateTime.now());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());
        db.cndJdbc(Customer.class).insert(customer1);

        company.setCustomers(Lists.newArrayList(customer, customer1));
        db.cndJdbc(Company.class).insert(company);
        Assert.assertEquals(2, db.cndJdbc(Customer.class).count());
        Assert.assertEquals(1, db.cndJdbc(Company.class).count());
    }

    @Test
    public void batchInsert() throws Exception {
        db.cndJdbc(Company.class).delete();
        db.cndJdbc(Customer.class).delete();

        Company company = new Company();
        company.setName("test");
        company.setId(uidGenerator.getUID());
        company.setCreateTime(new Date());
        company.setEnable(1);

        Customer customer = new Customer();
        if (!isSequence)
            customer.setId(uidGenerator.getUID());
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setStartTime(LocalDateTime.now());
        customer.setEnable(1);
        customer.setCompanyId(company.getId());

        Customer customer1 = new Customer();
        if (!isSequence)
            customer1.setId(uidGenerator.getUID());
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setStartTime(LocalDateTime.now());
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());

        db.cndJdbc(Company.class).insertBatch(Lists.newArrayList(company));
        db.cndJdbc(Customer.class).insertBatch(Lists.newArrayList(customer, customer1));

        Assert.assertEquals(2, db.cndJdbc(Customer.class).count());
        Assert.assertEquals(1, db.cndJdbc(Company.class).count());
    }

    @Test
    public void update() {
        Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        customer.setUsername("jack-0");
        db.cndJdbc(Customer.class).update(customer);
        customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assert.assertNull(customer);
        customer = db.cndJdbc(Customer.class).whereEq("username", "jack-0").single();
        Assert.assertNotNull(customer);
    }

    @Test
    public void batchUpdate() {
        List<Customer> customers = db.cndJdbc(Customer.class).select();
        customers = customers.stream().map(a -> {
            a.setUsername(a.getUsername().replace("jack", "tom"));
            return a;
        }).collect(Collectors.toList());
        int[] arr = db.cndJdbc(Customer.class).updateBatch(customers);
        for (int e : arr) {
            Assert.assertEquals(e, 1);
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
                Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
                customer.setScore(customer.getScore().subtract(BigDecimal.valueOf(1)));
                int rows = db.cndJdbc(Customer.class).update(customer);
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
        Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assert.assertTrue(1000L - customer.getScore().longValue() == atomicInteger.get() && atomicInteger.get() == customer.getVersion());
    }


    @Test
    public void delete() {
        db.cndJdbc(Customer.class).whereLike("username", "jack%").delete();
        List<Customer> customers = db.cndJdbc(Customer.class).select();
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }

}
