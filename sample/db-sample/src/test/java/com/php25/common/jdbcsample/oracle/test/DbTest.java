package com.php25.common.jdbcsample.oracle.test;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.oracle.dto.CustomerDto;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
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
 * @author penghuiping
 * @date 2018/8/31 10:27
 */
public class DbTest {


    @Autowired
    JdbcTemplate jdbcTemplate;

    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    List<Customer> customers = Lists.newArrayList();

    List<CustomerDto> customerDtos = Lists.newArrayList();

    private static boolean isFirst = true;

    private void initMeta() throws Exception {
        Class cls = Class.forName("oracle.jdbc.driver.OracleDriver");
        Driver driver = (Driver) cls.newInstance();
        Properties properties = new Properties();
        properties.setProperty("user", "system");
        properties.setProperty("password", "oracle");
        Connection connection = driver.connect("jdbc:oracle:thin:@localhost:1521:xe", properties);
        Statement statement = connection.createStatement();
        if (isFirst) {
            isFirst = false;
            statement.execute("create table t_customer (id number(38,0) primary key,username nvarchar2(20),password nvarchar2(50),age integer ,create_time date,update_time date,version number(38,0),company_id number(38,0),score number(32,0),enable number(1,0))");
            statement.execute("create table t_company (id number(38,0) primary key,name nvarchar2(20),create_time date,update_time date,enable number(1,0))");
            statement.execute("create table t_department (id number(38,0) primary key,name nvarchar2(20))");
            statement.execute("create table t_customer_department (customer_id number(38,0),department_id number(38,0))");
            statement.execute("CREATE SEQUENCE SEQ_ID");
        } else {
            statement.execute("drop table t_customer");
            statement.execute("drop table t_company");
            statement.execute("drop table t_department");
            statement.execute("drop table t_customer_department");
            statement.execute("create table t_customer (id number(38,0) primary key,username nvarchar2(20),password nvarchar2(50),age integer ,create_time date,update_time date,version number(38,0),company_id number(38,0),score number(32,0),enable number(1,0))");
            statement.execute("create table t_company (id number(38,0) primary key,name nvarchar2(20),create_time date,update_time date,enable number(1,0))");
            statement.execute("create table t_department (id number(38,0) primary key,name nvarchar2(20))");
            statement.execute("create table t_customer_department (customer_id number(38,0),department_id number(38,0))");
            statement.execute("drop SEQUENCE SEQ_ID");
            statement.execute("CREATE SEQUENCE SEQ_ID");
        }
        statement.close();
        connection.close();
    }

    @Before
    public void before() throws Exception {
        initMeta();

        Company company = new Company();
        company.setId(snowflakeIdWorker.nextId());
        company.setName("Google");
        company.setCreateTime(new Date());
        company.setNew(true);
        company.setEnable(1);
        SqlParams sqlParams = Queries.oracle().from(Company.class).insert(company);
        QueriesExecute.oracle().singleJdbc().with(jdbcTemplate).insert(sqlParams);

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.md5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setCompanyId(company.getId());
            customer.setUpdateTime(LocalDateTime.now());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams1 = Queries.oracle().from(Customer.class).insert(customer);
            QueriesExecute.oracle().singleJdbc().with(jdbcTemplate).insert(sqlParams1);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.md5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customer.setCompanyId(company.getId());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams1 = Queries.oracle().from(Customer.class).insert(customer);
            QueriesExecute.oracle().singleJdbc().with(jdbcTemplate).insert(sqlParams1);
            Assert.assertNotNull(customer.getId());
        }

        customerDtos = customers.stream().map(customer -> {
            CustomerDto customerDto = new CustomerDto();
            BeanUtils.copyProperties(customer, customerDto);
            return customerDto;
        }).collect(Collectors.toList());
    }
}
