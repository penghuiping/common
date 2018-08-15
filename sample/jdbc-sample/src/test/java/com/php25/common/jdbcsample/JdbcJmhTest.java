package com.php25.common.jdbcsample;

import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.jdbc.Cnd;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbcsample.model.Customer;
import org.openjdk.jmh.annotations.*;
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

import java.util.Date;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/14 17:01
 * @Description:
 */
@State(Scope.Benchmark)
public class JdbcJmhTest {
    private JdbcTemplate jdbcTemplate;

    private IdGeneratorService idGeneratorService;

    private Db db;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JdbcJmhTest.class.getSimpleName())
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
        DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext(JdbcTest.class, new DefaultCacheAwareContextLoaderDelegate());
        bootstrapper.setBootstrapContext(defaultBootstrapContext);
        TestContext testContext = bootstrapper.buildTestContext();

        this.jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);
        this.idGeneratorService = testContext.getApplicationContext().getBean(IdGeneratorService.class);
        this.db = new Db(this.jdbcTemplate);

        jdbcTemplate.update("drop table if exists t_customer;create table t_customer (id bigint,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");
        Cnd cnd = this.db.cnd(Customer.class);
        List<Customer> customers = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setCreateTime(new Date());
            customer.setEnable(1);
            customers.add(customer);
            cnd.insert(customer);
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void measureName() throws Exception {
        List<Customer> customers1 = this.db.cnd(Customer.class).whereEq("username", "jack0").limit(0, 1).asc("id").select();
    }
}
