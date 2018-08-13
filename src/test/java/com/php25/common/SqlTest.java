package com.php25.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.model.Customer;
import com.php25.common.repository.CustomerRepository;
import com.php25.common.repository.impl.BaseRepositoryImpl;
import com.php25.common.service.CustomerService;
import com.php25.common.service.IdGeneratorService;
import com.php25.common.sql.Cnd;
import com.php25.common.sql.Db;
import com.php25.common.util.DigestUtil;
import com.php25.common.util.JsonUtil;
import com.php25.common.util.TimeUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@DataJpaTest
@EntityScan(basePackages = {"com.php25"})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class SqlTest {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerService customerService;
    @Autowired
    IdGeneratorService idGeneratorService;
    @Autowired
    ObjectMapper objectMapper;
    private Logger log = LoggerFactory.getLogger(SqlTest.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestEntityManager entityManager;

    @Before
    public void save() throws Exception {
        Long start = TimeUtil.getCurrentTimeMillis();
        Cnd cnd = new Db().cnd(Customer.class);
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

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setCreateTime(new Date());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customers.add(customer);
            cnd.insert(customer);
        }
    }

    @Test
    public void query() {
        List<Customer> customers1 = new Db().cnd(Customer.class)
                .whereEq("username", "jack0")
                .orEq("username", "jack1")
                .limit(0, 1).asc("id").select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }

    @Test
    public void groupBy() {
        Db db = new Db();
        Cnd cnd = db.cnd(Customer.class);
        List<Map> customers1 = cnd.groupBy("enable").having(cnd.condition().andGreat("avg_age", 10)).mapSelect("avg(age) as avg_age", "enable");
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }

    @Test
    public void findOne() {
        Customer customer = new Db().cnd(Customer.class).whereEq("username", "jack0").single();
        System.out.println(JsonUtil.toPrettyJson(customer));
    }

    @Test
    public void count() {
        Long count = new Db().cnd(Customer.class).whereEq("username", "jack0").count();
        System.out.println(count);
    }


    @Test
    public void update() {
        Db db = new Db();
        Customer customer = db.cnd(Customer.class).whereEq("username", "jack0").single();
        customer.setUsername("jack-0");
        db.cnd(Customer.class).update(customer);
        customer = db.cnd(Customer.class).whereEq("username", "jack0").single();
        Assert.assertNull(customer);
        customer = db.cnd(Customer.class).whereEq("username", "jack-0").single();
        Assert.assertNotNull(customer);

    }

    @Test
    public void delete() {
        Db db = new Db();
        List<Customer> customers1 = db.cnd(Customer.class).select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
        db.cnd(Customer.class).andEq("username", "jack0").delete();
        customers1 = db.cnd(Customer.class).select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }

}
