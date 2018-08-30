package com.php25.common.jdbcsample;

import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbcsample.model.Customer;
import com.php25.common.jdbcsample.repository.CustomerRepository;
import org.junit.Assert;
import org.junit.Before;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/16 23:01
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class MysqlRepositoryTest {

    @Autowired
    IdGeneratorService idGeneratorService;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Db db;

    Logger log = LoggerFactory.getLogger(MysqlRepositoryTest.class);

    List<Customer> customers;

    boolean isAutoIncrement = false;

    @Before
    public void before() {

        jdbcTemplate.batchUpdate("drop table if exists t_customer");
        jdbcTemplate.batchUpdate("drop table if exists t_company");
        if (isAutoIncrement) {
            jdbcTemplate.batchUpdate("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,company_id bigint)");
            jdbcTemplate.batchUpdate("create table t_company (id bigint auto_increment primary key,name varchar(20),create_time date,update_time date,`enable` int)");
        } else {
            jdbcTemplate.batchUpdate("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,company_id bigint)");
            jdbcTemplate.batchUpdate("create table t_company (id bigint primary key,name varchar(20),create_time date,update_time date,`enable` int)");
        }

        customers = Lists.newArrayList();
        for (int i = 0; i <= 3; i++) {
            Customer customer = new Customer();
            customer.setId(new Long(i));
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(new Date());
            if (i % 2 == 0)
                customer.setEnable(1);
            else
                customer.setEnable(0);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
    }

    @Test
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assert.assertEquals(customers.size(), this.customers.size() / 2);
    }

    @Test
    public void findAllSort() {
        Iterable<Customer> customers = customerRepository.findAll(Sort.by(Sort.Order.desc("id")));
        Assert.assertEquals(Lists.newArrayList(customers).size(), this.customers.size());
        Assert.assertEquals(Lists.newArrayList(customers).get(0).getId(), this.customers.get(this.customers.size() - 1).getId());
    }

    @Test
    public void findAllPage() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(page);
        log.info(JsonUtil.toPrettyJson(customers));
    }

    @Test
    public void save() {
        //新增
        Customer customer = new Customer();
        customer.setId(new Long(4));
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(new Date());
        customer.setAge(4 * 10);
        customerRepository.save(customer);
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));

        //更新
        Optional<Customer> customerOptional = customerRepository.findById(4L);
        customer = customerOptional.get();
        customer.setUsername("jack" + 5);
        customer.setUpdateTime(new Date());
        customerRepository.save(customer);

        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void saveAll() {
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void findById() {
        Customer customer = customers.get(0);
        customerRepository.findById(customer.getId());
        Assert.assertEquals(customer.getId(), customers.get(0).getId());
    }

    @Test
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
        Assert.assertTrue(result);
    }

    @Test
    public void findAll() {
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
    public void count() {
        Assert.assertEquals(customerRepository.count(), customers.size());
    }

    @Test
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void deleteAll() {
        customerRepository.deleteAll();
        log.info(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }
}
