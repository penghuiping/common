package com.php25.common.jdbcsample.oracle.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.oracle.CommonAutoConfigure;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import com.php25.common.jdbcsample.oracle.repository.CompanyRepository;
import com.php25.common.jdbcsample.oracle.repository.CustomerRepository;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategyTarget;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @ClassRule
    public static GenericContainer oracle = new GenericContainer<>("wnameless/oracle-xe-11g-r2")
            .withExposedPorts(1521)
            .waitingFor(new AbstractWaitStrategy() {
                @Override
                protected void waitUntilReady() {
                    try {
                        Thread.sleep(1000*30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

    static {
        oracle.setPortBindings(Lists.newArrayList("49161:1521"));
        oracle.withEnv("ORACLE_ALLOW_REMOTE", "true");
        oracle.withEnv("ORACLE_DISABLE_ASYNCH_IO", "true");
    }


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
        Long count = db.cndJdbc(Customer.class).whereEq("enable", 1).count();
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

        log.info("customers:{}", JsonUtil.toPrettyJson(db.cndJdbc(Customer.class).select()));
        log.info("companys:{}", JsonUtil.toPrettyJson(db.cndJdbc(Company.class).select()));
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
        customers = db.cndJdbc(Customer.class).select();
        List<Customer> customers1 = customers.stream().filter(customer -> customer.getUsername().startsWith("tom")).collect(Collectors.toList());
        Assert.assertTrue(customers1.size()==3);
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

    /**
     * repository test
     *
     */

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getEnable() == 1).count());
    }

    @Test
    public void findAllSort() {
        Iterable<Customer> customers = customerRepository.findAll(SearchParamBuilder.builder(), Sort.by(Sort.Order.desc("id")));
        Assert.assertEquals(Lists.newArrayList(customers).size(), this.customers.size());
        Assert.assertEquals(Lists.newArrayList(customers).get(0).getId(), this.customers.get(this.customers.size() - 1).getId());
    }

    @Test
    public void findAllPage() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(SearchParamBuilder.builder(), page);
        Assert.assertEquals(customers.getContent().size(), 2);
    }

    @Test
    public void save() {
        //新增
        Company company = new Company();
        company.setId(uidGenerator.getUID());
        company.setName("baidu");
        company.setEnable(1);
        company.setCreateTime(new Date());
        company.setNew(true);
        companyRepository.save0(company);
        SearchParamBuilder builder = SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "baidu"));
        Assert.assertEquals(companyRepository.findOne(builder).get().getName(), "baidu");

        Customer customer = new Customer();
        if (!isSequence)
            customer.setId(uidGenerator.getUID());
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(4 * 10);
        customer.setCompanyId(company.getId());
        customer.setNew(true);
        customerRepository.save0(customer);
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Assert.assertEquals(customerRepository.findOne(builder).get().getUsername(), "jack4");

        //更新
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Optional<Customer> customerOptional = customerRepository.findOne(builder);
        customer = customerOptional.get();
        customer.setUsername("jack" + 5);
        customer.setUpdateTime(LocalDateTime.now());
        customer.setNew(false);
        customerRepository.save0(customer);

        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack5"));
        customerOptional = customerRepository.findOne(builder);
        Assert.assertEquals(customerOptional.get().getUsername(), "jack" + 5);
    }

    @Test
    public void saveAll() {
        customerRepository.deleteAll();
        //保存
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isSequence)
                customer.setId(uidGenerator.getUID());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge((i + 1) * 10);
            customer.setNew(true);
            customers.add(customer);
        }
        customerRepository.saveAll0(customers);
        Assert.assertEquals(Lists.newArrayList(customerRepository.findAll()).size(), 3);

        //更新
        customers = Lists.newArrayList(customerRepository.findAll());
        for (int i = 0; i < 3; i++) {
            customers.get(i).setUsername("mary" + i);
            customers.get(i).setNew(false);
        }
        customerRepository.saveAll0(customers);

        System.out.println(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void findById() {
        Customer customer = customers.get(0);
        Optional<Customer> customer1 = customerRepository.findById(customer.getId());
        Assert.assertEquals(customer1.get().getId(), customer.getId());
    }

    @Test
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
        Assert.assertTrue(result);
    }

    @Test
    public void _findAll() {
        Iterable iterable = customerRepository.findAll();
        Assert.assertEquals(Lists.newArrayList(iterable).size(), customers.size());
    }

    @Test
    public void findAllById() {
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Iterable iterable = customerRepository.findAllById(ids);
        Assert.assertEquals(Lists.newArrayList(iterable).size(), customers.size());
    }

    @Test
    public void _count() {
        Assert.assertEquals(customerRepository.count(), customers.size());
    }

    @Test
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
        Assert.assertEquals(customerRepository.count(), customers.size() - 1);
        Assert.assertFalse(customerRepository.existsById(customers.get(0).getId()));
    }

    @Test
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
        Assert.assertEquals(customerRepository.count(), customers.size() - 1);
        Assert.assertFalse(customerRepository.existsById(customers.get(1).getId()));
    }

    @Test
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
        customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList()).forEach(customer -> {
            Assert.assertFalse(customerRepository.existsById(customer.getId()));
        });
    }

    @Test
    public void deleteAll() {
        customerRepository.deleteAll();
        Assert.assertEquals(customerRepository.count(), 0);
    }
}
