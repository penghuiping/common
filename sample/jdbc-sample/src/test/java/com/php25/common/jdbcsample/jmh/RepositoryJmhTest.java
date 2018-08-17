package com.php25.common.jdbcsample.jmh;

import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.IdGeneratorServiceImpl;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.jdbcsample.MysqlJdbcTest;
import com.php25.common.jdbcsample.model.Customer;
import com.php25.common.jdbcsample.repository.CustomerRepository;
import com.php25.common.jdbcsample.repository.CustomerRepositoryImpl;
import org.openjdk.jmh.annotations.*;
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

import java.util.Date;
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

        customerRepository = testContext.getApplicationContext().getBean(CustomerRepositoryImpl.class);
        idGeneratorService = testContext.getApplicationContext().getBean(IdGeneratorServiceImpl.class);
        jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

        jdbcTemplate.update("create table t_customer (id bigint,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");


        customers = Lists.newArrayList();
        for (int i = 0; i <= 3; i++) {
            Customer customer = new Customer();
            customer.setId(new Long(i));
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setCreateTime(new Date());
            if (i % 2 == 0)
                customer.setEnable(1);
            else
                customer.setEnable(0);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findAllSort() {
        Iterable<Customer> customers = customerRepository.findAll(Sort.by(Sort.Order.desc("id")));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findAllPage() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(page);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void save() {
        Customer customer = new Customer();
        customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(4 * 10);
        customerRepository.save(customer);
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void findById() {
        Customer customer = customers.get(0);
        customerRepository.findById(customer.getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findAll() {
        Iterable iterable = customerRepository.findAll();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findAllById() {
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Iterable iterable = customerRepository.findAllById(ids);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void count() {
        customerRepository.count();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void deleteAll() {
        customerRepository.deleteAll();
    }


}
