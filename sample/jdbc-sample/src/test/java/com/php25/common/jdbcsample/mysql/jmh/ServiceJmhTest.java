package com.php25.common.jdbcsample.mysql.jmh;

import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.IdGeneratorServiceImpl;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbcsample.mysql.dto.CustomerDto;
import com.php25.common.jdbcsample.mysql.service.CustomerService;
import com.php25.common.jdbcsample.mysql.service.CustomerServiceImpl;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.support.DefaultBootstrapContext;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * @author: penghuiping
 * @date: 2019/3/17 23:22
 * @description:
 */
@State(Scope.Benchmark)
public class ServiceJmhTest {

    List<CustomerDto> customers;

    private CustomerService customerService;

    private IdGeneratorService idGeneratorService;

    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ServiceJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("10s"))
                .warmupIterations(1)
                .warmupTime(TimeValue.valueOf("10s"))
                .measurementIterations(1)
                .measurementTime(TimeValue.valueOf("20s"))
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

        customerService = testContext.getApplicationContext().getBean(CustomerServiceImpl.class);
        idGeneratorService = testContext.getApplicationContext().getBean(IdGeneratorServiceImpl.class);
        jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

        jdbcTemplate.execute("drop table if exists t_customer");
        jdbcTemplate.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,score bigint,company_id bigint)");

        customers = Lists.newArrayList();
        for (int i = 0; i <= 3; i++) {
            CustomerDto customer = new CustomerDto();
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
        customerService.save(customers);
    }

    @Benchmark
    public void findOne() {
        Optional<CustomerDto> customerDtoOptional = customerService.findOne(0L);
    }

    @Benchmark
    public void findOneAsync() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(1);
            Mono<Optional<CustomerDto>> customerDtoMono = customerService.findOneAsync(0L);
            customerDtoMono.subscribe(customerDto -> {
            }, throwable -> {

            }, () -> {
                countDownLatch.countDown();
            });
        countDownLatch.await();
    }
}
