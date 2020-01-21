package com.php25.common.jdbcsample.postgres.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.db.specification.Operator;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;
import com.php25.common.jdbcsample.postgres.CommonAutoConfigure;
import com.php25.common.jdbcsample.postgres.model.Company;
import com.php25.common.jdbcsample.postgres.model.Customer;
import com.php25.common.jdbcsample.postgres.model.Department;
import com.php25.common.jdbcsample.postgres.model.DepartmentRef;
import com.php25.common.jdbcsample.postgres.repository.CompanyRepository;
import com.php25.common.jdbcsample.postgres.repository.CustomerRepository;
import com.php25.common.jdbcsample.postgres.repository.DepartmentRepository;
import org.assertj.core.api.Assertions;
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

import java.math.BigDecimal;
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
public class PostgresJdbcTest extends DbTest {

    private Logger log = LoggerFactory.getLogger(PostgresJdbcTest.class);

    @ClassRule
    public static GenericContainer postgres = new GenericContainer<>("postgres:12.0-alpine")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_USER", "root")
            .withEnv("POSTGRES_PASSWORD", "root")
            .withEnv("POSTGRES_DB", "test");

    static {
        postgres.setPortBindings(org.assertj.core.util.Lists.newArrayList("5432:5432"));
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UidGenerator uidGenerator;

    @Override
    protected void initDb() {
        this.db = new Db(DbType.POSTGRES);
        this.db.setJdbcOperations(jdbcTemplate);
    }

    @Test
    public void query() {
        //like
        List<Customer> customers = db.cndJdbc(Customer.class)
                .whereLike("username", "jack%").asc("id").select();
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        customers = db.cndJdbc(Customer.class)
                .whereNotLike("username", "jack%").asc("id").select();
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        Company company = db.cndJdbc(Company.class).whereEq("name", "Google").single();
        Assertions.assertThat(company).isNotNull();

        //not eq
        company = db.cndJdbc(Company.class).whereNotEq("name", "Google").single();
        Assertions.assertThat(company).isNull();

        //between...and..
        customers = db.cndJdbc(Customer.class).whereBetween("age", 20, 50).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        customers = db.cndJdbc(Customer.class).whereNotBetween("age", 20, 50).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        customers = db.cndJdbc(Customer.class).whereIn("age", Lists.newArrayList(20, 40)).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        customers = db.cndJdbc(Customer.class).whereNotIn("age", Lists.newArrayList(0, 10)).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        customers = db.cndJdbc(Customer.class).whereGreat("age", 40).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        customers = db.cndJdbc(Customer.class).whereGreatEq("age", 40).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        customers = db.cndJdbc(Customer.class).whereLess("age", 0).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        customers = db.cndJdbc(Customer.class).whereLessEq("age", 0).select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        customers = db.cndJdbc(Customer.class).whereIsNull("updateTime").select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        customers = db.cndJdbc(Customer.class).whereIsNotNull("updateTime").select();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //join
        customers = db.cndJdbc(Customer.class).join(Company.class, "id", "companyId").select(Customer.class);
        System.out.println(JsonUtil.toPrettyJson(customers));
        Assertions.assertThat(customers.size()).isEqualTo(6);

        List<Company> companies = db.cndJdbc(Customer.class).join(Company.class, "id", "companyId").whereEq(Company.class, "name", "Google").select(Company.class);
        System.out.println(JsonUtil.toPrettyJson(companies));
        Assertions.assertThat(customers.size()).isEqualTo(6);

    }


    @Test
    public void orderBy() {
        List<Customer> customers = db.cndJdbc(Customer.class).orderBy("age asc").select();
        List<Customer> customers1 = db.cndJdbc(Customer.class).asc("age").select();
        Assertions.assertThat(customers.size()).isEqualTo(customers1.size());
        for (int i = 0; i < customers.size(); i++) {
            Assertions.assertThat(customers.get(i).getAge()).isEqualTo(customers1.get(i).getAge());
        }
    }

    @Test
    public void groupBy() {
        CndJdbc cndJdbc = db.cndJdbc(Customer.class);
        List<Map> customers1 = cndJdbc.groupBy("enable").mapSelect("avg(age) as avg_age", "enable");
        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(Customer::getEnable, Collectors.averagingInt(Customer::getAge)));
        System.out.println(JsonUtil.toPrettyJson(result));
        Assertions.assertThat(customers1).isNotNull();
        Assertions.assertThat(customers1.size()>0);
        for (Map map : customers1) {
            Assertions.assertThat(BigDecimal.valueOf(result.get(map.get("enable"))).intValue()).isEqualTo(((BigDecimal) map.get("avg_age")).intValue());
        }
    }

    @Test
    public void findAll() {
        List<Customer> customers = db.cndJdbc(Customer.class).select();
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.size());
    }

    @Test
    public void findOne() {
        Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assertions.assertThat(customer).isNotNull();
        Assertions.assertThat("jack0").isEqualTo(customer.getUsername());
    }

    @Test
    public void count() {
        Long count = db.cndJdbc(Customer.class).whereEq("enable", 1).count();
        Assertions.assertThat(this.customers.stream().filter(a -> a.getEnable() == 1).count()).isEqualTo((long)count);
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
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        customer.setCompanyId(company.getId());
        db.cndJdbc(Customer.class).insert(customer);

        Customer customer1 = new Customer();
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
        Assertions.assertThat(2).isEqualTo(db.cndJdbc(Customer.class).count());
        Assertions.assertThat(1).isEqualTo(db.cndJdbc(Company.class).count());
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

        db.cndJdbc(Company.class).insertBatch(Lists.newArrayList(company));
        db.cndJdbc(Customer.class).insertBatch(Lists.newArrayList(customer, customer1));

        Assertions.assertThat(2).isEqualTo(db.cndJdbc(Customer.class).count());
        Assertions.assertThat(1).isEqualTo(db.cndJdbc(Company.class).count());
    }

    @Test
    public void update() {
        Customer customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        customer.setUsername("jack-0");
        db.cndJdbc(Customer.class).update(customer);
        customer = db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assertions.assertThat(customer).isNull();
        customer = db.cndJdbc(Customer.class).whereEq("username", "jack-0").single();
        Assertions.assertThat(customer).isNotNull();
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
        Assertions.assertThat(1000L - customer.getScore().longValue()).isEqualTo(atomicInteger.get());
        Assertions.assertThat(customer.getVersion()).isEqualTo(atomicInteger.get());
    }


    @Test
    public void delete() {
        db.cndJdbc(Customer.class).whereLike("username", "jack%").delete();
        List<Customer> customers = db.cndJdbc(Customer.class).select();
        Assertions.assertThat(customers).isNotNull();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }

    /**
     * repository test
     */


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @Test
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getEnable() == 1).count());
    }

    @Test
    public void findAllSort() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder().append(Lists.newArrayList());
        Iterable<Customer> customers = customerRepository.findAll(searchParamBuilder, Sort.by(Sort.Order.desc("id")));
        Assertions.assertThat(Lists.newArrayList(customers).size()).isEqualTo(this.customers.size());
        Assertions.assertThat(Lists.newArrayList(customers).get(0).getId()).isEqualTo(this.customers.get(this.customers.size() - 1).getId());
    }

    @Test
    public void findAllPage() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder().append(Lists.newArrayList());
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(searchParamBuilder, page);
        Assertions.assertThat(customers.getContent().size()).isEqualTo(2);
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
        companyRepository.save(company);
        SearchParamBuilder builder = SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "baidu"));
        Assertions.assertThat(companyRepository.findOne(builder).get().getName()).isEqualTo("baidu");

        Customer customer = new Customer();
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(4 * 10);
        customer.setCompanyId(company.getId());
        customer.setNew(true);
        customerRepository.save(customer);
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Assertions.assertThat(customerRepository.findOne(builder).get().getUsername()).isEqualTo("jack4");

        //更新
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Optional<Customer> customerOptional = customerRepository.findOne(builder);
        customer = customerOptional.get();
        customer.setUsername("jack" + 5);
        customer.setUpdateTime(LocalDateTime.now());
        customer.setNew(false);

        customerRepository.save(customer);
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack5"));
        customerOptional = customerRepository.findOne(builder);
        Assertions.assertThat(customerOptional.get().getUsername()).isEqualTo("jack" + 5);
    }

    @Test
    public void saveAll() {
        customerRepository.deleteAll();
        //保存
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge((i + 1) * 10);
            customer.setNew(true);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
        Assert.assertEquals(Lists.newArrayList(customerRepository.findAll()).size(), 3);

        //更新
        customers = Lists.newArrayList(customerRepository.findAll());
        for (int i = 0; i < 3; i++) {
            customers.get(i).setUsername("mary" + i);
            customers.get(i).setNew(false);
        }
        customerRepository.saveAll(customers);
        Assertions.assertThat(Lists.newArrayList(customerRepository.findAll()).stream().filter(customer -> customer.getUsername().startsWith("mary")).count()).isEqualTo(3);
    }

    @Test
    public void findById() {
        Customer customer = customers.get(0);
        Optional<Customer> customer1 = customerRepository.findById(customer.getId());
        Assertions.assertThat(customer1.get().getId()).isEqualTo(customer.getId());
    }

    @Test
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void _findAll() {
        Iterable iterable = customerRepository.findAll();
        Assertions.assertThat(Lists.newArrayList(iterable).size()).isEqualTo(customers.size());
    }

    @Test
    public void findAllById() {
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Iterable iterable = customerRepository.findAllById(ids);
        Assertions.assertThat(Lists.newArrayList(iterable).size()).isEqualTo(customers.size());
    }

    @Test
    public void _count() {
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size());
    }

    @Test
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size()-1);
        Assertions.assertThat(customerRepository.existsById(customers.get(0).getId())).isFalse();
    }

    @Test
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size()-1);
        Assertions.assertThat(customerRepository.existsById(customers.get(1).getId())).isFalse();
    }

    @Test
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
        customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList()).forEach(customer -> {
            Assertions.assertThat(customerRepository.existsById(customer.getId())).isFalse();
        });
    }

    @Test
    public void deleteAll() {
        customerRepository.deleteAll();
        Assertions.assertThat(customerRepository.count()).isEqualTo(0);
    }

    @Test
    public void testManyToMany() {
        //部门
        Department department = new Department();
        department.setId(uidGenerator.getUID());
        department.setName("testDepart");
        department.setNew(true);
        department = departmentRepository.save(department);

        Department department1 = new Department();
        department1.setId(uidGenerator.getUID());
        department1.setName("testDepart1");
        department1.setNew(true);
        department1 = departmentRepository.save(department1);

        //人员
        Customer customer = new Customer();
        customer.setUsername("jack12313");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(10);
        customer.setNew(true);
        customer.setEnable(1);

        DepartmentRef departmentRef = new DepartmentRef();
        departmentRef.setDepartmentId(department.getId());

        customer.setDepartments(Sets.newHashSet(departmentRef));
        customer = customerRepository.save(customer);

        Department _department = departmentRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "testDepart"))).get();
        Assertions.assertThat(department.getId()).isEqualTo(_department.getId());

        Customer _customer = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313"))).get();
        Assertions.assertThat(_customer.getId()).isEqualTo(customer.getId());
        Assertions.assertThat(_customer.getDepartments().size()).isEqualTo(1);

        DepartmentRef departmentRef1 = new DepartmentRef();
        departmentRef1.setDepartmentId(department1.getId());

        customer.setDepartments(Sets.newHashSet(departmentRef, departmentRef1));
        customer.setNew(false);
        customer = customerRepository.save(customer);

        _customer = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313"))).get();
        Assertions.assertThat(_customer.getDepartments().size()).isEqualTo(2);

        customerRepository.deleteAll(Lists.newArrayList(customer));

        Optional optional = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313")));
        Assertions.assertThat(optional.isPresent()).isEqualTo(false);

    }

}
