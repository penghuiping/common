package com.php25.common.jdbcsample.mysql.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGenerator;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.mysql.dto.CustomerDto;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author: penghuiping
 * @date: 2018/8/31 10:27
 * @description:
 */
public class DbTest {

    @Autowired
    IdGenerator idGeneratorService;

    @Autowired
    UidGenerator uidGenerator;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Db db;

    boolean isAutoIncrement = true;

    List<Customer> customers = Lists.newArrayList();

    List<CustomerDto> customerDtos = Lists.newArrayList();

    protected void initDb() {

    }

    private void initMeta(boolean isAutoIncrement) throws Exception {
        Class cls = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) cls.newInstance();
        Properties properties = new Properties();
        properties.setProperty("user","root");
        properties.setProperty("password","root");
        Connection connection = driver.connect("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false", properties);
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_customer");
        statement.execute("drop table if exists t_company");
        if (isAutoIncrement) {
            statement.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
            statement.execute("create table t_company (id bigint auto_increment primary key,name varchar(20),create_time datetime,update_time datetime,`enable` int)");
        } else {
            statement.execute("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
            statement.execute("create table t_company (id bigint primary key,name varchar(20),create_time datetime,update_time datetime,`enable` int)");
        }
        statement.close();
        connection.close();
    }

    @Before
    public void before() throws Exception {
        initMeta(isAutoIncrement);
        this.initDb();
        CndJdbc cndJdbc = db.cndJdbc(Customer.class);
        CndJdbc cndJdbcCompany = db.cndJdbc(Company.class);


        Company company = new Company();
        company.setName("Google");
        company.setId(uidGenerator.getUID());
        company.setCreateTime(new Date());
        company.setEnable(1);
        cndJdbcCompany.insert(company);


        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(uidGenerator.getUID());
            }
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setCompanyId(company.getId());
            customer.setUpdateTime(LocalDateTime.now());
            customer.setScore(BigDecimal.valueOf(1000L));
            customers.add(customer);
            cndJdbc.insert(customer);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(uidGenerator.getUID());
            }
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customer.setCompanyId(company.getId());
            customer.setScore(BigDecimal.valueOf(1000L));
            customers.add(customer);
            cndJdbc.insert(customer);
            Assert.assertNotNull(customer.getId());
        }

        customerDtos = customers.stream().map(customer -> {
            CustomerDto customerDto = new CustomerDto();
            BeanUtils.copyProperties(customer, customerDto);
            return customerDto;
        }).collect(Collectors.toList());
    }
}
