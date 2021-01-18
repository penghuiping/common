package com.php25.common.jdbcsample.mysql.test;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.mysql.dto.CustomerDto;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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
    JdbcTemplate jdbcTemplate;

    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    List<Customer> customers = Lists.newArrayList();

    List<CustomerDto> customerDtos = Lists.newArrayList();

    private void initMeta() throws Exception {
        jdbcTemplate.execute("drop table if exists t_customer");
        jdbcTemplate.execute("drop table if exists t_company");
        jdbcTemplate.execute("drop table if exists t_department");
        jdbcTemplate.execute("drop table if exists t_customer_department");
        jdbcTemplate.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
        jdbcTemplate.execute("create table t_company (id bigint primary key,name varchar(20),create_time datetime,update_time datetime,`enable` int)");
        jdbcTemplate.execute("create table t_department (id bigint primary key,name varchar(20))");
        jdbcTemplate.execute("create table t_customer_department (customer_id bigint,department_id bigint)");
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
        SqlParams sqlParams = Queries.mysql().from(Company.class).insert(company);
        QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams);

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setCompanyId(company.getId());
            customer.setUpdateTime(LocalDateTime.now());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams1 = Queries.mysql().from(Customer.class).insert(customer);
            QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams1);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customer.setCompanyId(company.getId());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams1 = Queries.mysql().from(Customer.class).insert(customer);
            QueriesExecute.mysql().singleJdbc().with(jdbcTemplate).insert(sqlParams1);
            Assert.assertNotNull(customer.getId());
        }

        customerDtos = customers.stream().map(customer -> {
            CustomerDto customerDto = new CustomerDto();
            BeanUtils.copyProperties(customer, customerDto);
            return customerDto;
        }).collect(Collectors.toList());
    }
}
