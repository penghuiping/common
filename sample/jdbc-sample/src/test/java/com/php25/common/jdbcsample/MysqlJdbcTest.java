package com.php25.common.jdbcsample;

import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbc.Cnd;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class MysqlJdbcTest {

    @Autowired
    IdGeneratorService idGeneratorService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Db db;

    @Autowired
    CustomerRepository customerRepository;

    private Logger log = LoggerFactory.getLogger(MysqlJdbcTest.class);

    @Before
    public void save() throws Exception {
        jdbcTemplate.update("create table t_customer (id bigint,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");

        Cnd cnd = db.cnd(Customer.class);
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
        List<Customer> customers1 = db.cnd(Customer.class)
                .orEq("username", "jack1")
                .limit(0, 1).asc("id").select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }


    @Test
    public void groupBy() {
        Cnd cnd = db.cnd(Customer.class);
        List<Map> customers1 = cnd.groupBy("enable").having(cnd.condition().andGreat("avg_age", 10)).mapSelect("avg(age) as avg_age", "enable");
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }

    @Test
    public void findOne() {
        Customer customer = db.cnd(Customer.class).whereEq("username", "jack0").single();
        System.out.println(JsonUtil.toPrettyJson(customer));
    }

    @Test
    public void count() {
        Long count = db.cnd(Customer.class).whereEq("username", "jack0").count();
        System.out.println(count);
    }


    @Test
    public void update() {
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
        List<Customer> customers1 = db.cnd(Customer.class).select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
        db.cnd(Customer.class).andEq("username", "jack0").delete();
        customers1 = db.cnd(Customer.class).select();
        System.out.println(JsonUtil.toPrettyJson(customers1));
    }

    @Test
    public void test() {
        List<Customer> customers = customerRepository.findAllEnabled();
        return;
    }

}
