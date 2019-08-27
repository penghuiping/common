package com.php25.common.jdbcsample.mysql.jmh;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.IdGeneratorServiceImpl;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.jdbcsample.mysql.model.Customer;
import com.php25.common.jdbcsample.mysql.repository.CustomerRepository;
import com.php25.common.jdbcsample.mysql.repository.CustomerRepositoryImpl;
import com.php25.common.jdbcsample.mysql.test.MysqlJdbcTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.support.DefaultBootstrapContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/17 09:40
 * @Description:
 */
@State(Scope.Benchmark)
public class RepositoryJmhTest {

    List<Customer> customers;
    private CustomerRepository customerRepository;
    private IdGeneratorService idGeneratorService;
    private UidGenerator uidGenerator;
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(RepositoryJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("60s"))
                .warmupIterations(1)
                .warmupTime(TimeValue.valueOf("30s"))
                .measurementIterations(1)
                .measurementTime(TimeValue.valueOf("30s"))
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void init() {
        SpringBootTestContextBootstrapper bootstrapper = new SpringBootTestContextBootstrapper();
        DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext(MysqlJdbcTest.class, new DefaultCacheAwareContextLoaderDelegate());
        bootstrapper.setBootstrapContext(defaultBootstrapContext);
        TestContext testContext = bootstrapper.buildTestContext();

        customerRepository = testContext.getApplicationContext().getBean(CustomerRepository.class);
        idGeneratorService = testContext.getApplicationContext().getBean(IdGeneratorServiceImpl.class);
        uidGenerator = testContext.getApplicationContext().getBean(UidGenerator.class);
        jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

        jdbcTemplate.execute("drop table if exists t_customer");
        jdbcTemplate.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,score bigint,company_id bigint)");

        customers = Lists.newArrayList();
        for (int i = 0; i <= 3; i++) {
            Customer customer = new Customer();
            customer.setId(new Long(i));
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            if (i % 2 == 0)
                customer.setEnable(1);
            else
                customer.setEnable(0);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
    }

    @Benchmark
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
    }

    @Benchmark
    public void findAllSort() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder();
        Iterable<Customer> customers = customerRepository.findAll(searchParamBuilder,Sort.by(Sort.Order.desc("id")));
    }

    @Benchmark
    public void findAllPage() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder();
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(searchParamBuilder,page);
    }

    @Benchmark
    public void save() {
        Customer customer = new Customer();
        customer.setId(uidGenerator.getUID());
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(4 * 10);
        customerRepository.save(customer);
    }


    @Benchmark
    public void findById() {
        Customer customer = customers.get(0);
        customerRepository.findById(customer.getId());
    }

    @Benchmark
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
    }

    @Benchmark
    public void findAll() {
        Iterable iterable = customerRepository.findAll();
    }

    @Benchmark
    public void findAllById() {
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Iterable iterable = customerRepository.findAllById(ids);
    }

    @Benchmark
    public void count() {
        customerRepository.count();
    }

    @Benchmark
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
    }

    @Benchmark
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
    }

    @Benchmark
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
    }

    @Benchmark
    public void deleteAll() {
        customerRepository.deleteAll();
    }


}
