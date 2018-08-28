package com.php25.common.jdbcsample;

import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbc.Cnd;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbc.DbType;
import com.php25.common.jdbcsample.model.Customer;
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
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

    Db db;

    private boolean isAutoIncrement = false;

    private Logger log = LoggerFactory.getLogger(MysqlJdbcTest.class);

    public void initMeta(boolean isAutoIncrement) throws Exception {
        Class cls = Class.forName("org.h2.Driver");
        Driver driver = (Driver) cls.newInstance();
        Connection connection = driver.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL", null);
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_customer");
        if (isAutoIncrement) {
            statement.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");
        } else {
            statement.execute("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");
        }
        statement.closeOnCompletion();
        connection.close();
        this.db = new Db(jdbcTemplate, DbType.MYSQL);
    }

    @Before
    public void save() throws Exception {
        initMeta(isAutoIncrement);
        Cnd cnd = db.cnd(Customer.class);
        List<Customer> customers = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
            }
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setCreateTime(new Date());
            customer.setEnable(1);
            customers.add(customer);
            cnd.insert(customer);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
            }
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setCreateTime(new Date());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customers.add(customer);
            cnd.insert(customer);
            Assert.assertNotNull(customer.getId());
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
}
