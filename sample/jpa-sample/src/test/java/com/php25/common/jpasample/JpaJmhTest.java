package com.php25.common.jpasample;

import com.google.common.collect.Lists;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.jpasample.dto.CustomerDto;
import com.php25.common.jpasample.model.Customer;
import com.php25.common.jpasample.repository.CustomerRepository;
import com.php25.common.jpasample.service.CustomerService;
import org.junit.Assert;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.support.DefaultBootstrapContext;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/14 17:01
 * @Description:
 */
@State(Scope.Benchmark)
public class JpaJmhTest {


    List<Customer> customers;
    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private IdGeneratorService idGeneratorService;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JpaJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("60s"))
                .warmupIterations(1)
                .warmupTime(TimeValue.valueOf("10s"))
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
        DefaultBootstrapContext defaultBootstrapContext = new DefaultBootstrapContext(JpaTest.class, new DefaultCacheAwareContextLoaderDelegate());
        bootstrapper.setBootstrapContext(defaultBootstrapContext);
        TestContext testContext = bootstrapper.buildTestContext();
        customerService = testContext.getApplicationContext().getBean(CustomerService.class);
        customerRepository = testContext.getApplicationContext().getBean(CustomerRepository.class);

        this.idGeneratorService = testContext.getApplicationContext().getBean(IdGeneratorService.class);

        customers = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();
            customer.setId(this.idGeneratorService.getModelPrimaryKeyNumber().longValue());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setCreateTime(new Date());
            customer.setEnable(1);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
    }

    //@org.openjdk.jmh.annotations.Benchmark
    public void measureName() throws Exception {
        Optional<DataGridPageDto<CustomerDto>> customerDtos = customerService.query(1, 1, new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).value("jack0").build()), BeanUtils::copyProperties, Sort.by(Sort.Order.asc("id")));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void findOne() throws Exception {
        Optional<CustomerDto> customer = customerService.findOne(customers.get(0).getId());
        Assert.assertEquals(customer.get().getId(), customers.get(0).getId());
    }
}
