package com.php25.common.jdbcsample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.jdbcsample.dto.CustomerDto;
import com.php25.common.jdbcsample.mapper.CompanyMapper;
import com.php25.common.jdbcsample.mapper.CustomerMapper;
import com.php25.common.jdbcsample.model.Company;
import com.php25.common.jdbcsample.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */

public class DbTest {

    private static final Logger logger = LoggerFactory.getLogger(DbTest.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IdGeneratorService idGeneratorService;


    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CompanyMapper companyMapper;


    boolean isAutoIncrement = true;

    List<Customer> customers = Lists.newArrayList();

    List<CustomerDto> customerDtos = Lists.newArrayList();

    @Before
    public void save() throws Exception {
        initMeta(false);

        initMeta(isAutoIncrement);

        Company company = new Company();
        company.setName("Google");
        company.setId(idGeneratorService.getSnowflakeId().longValue());
        company.setCreateTime(new Date());
        company.setEnable(1);
        companyMapper.insert(company);

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(idGeneratorService.getSnowflakeId().longValue());
            }
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(new Date());
            customer.setEnable(1);
            customer.setVersion(0);
            customer.setUpdateTime(new Date());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setCompanyId(company.getId());
            customers.add(customer);
            customerMapper.insert(customer);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isAutoIncrement) {
                customer.setId(idGeneratorService.getSnowflakeId().longValue());
            }
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(new Date());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customer.setVersion(0);
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setCompanyId(company.getId());
            customers.add(customer);
            customerMapper.insert(customer);
            Assert.assertNotNull(customer.getId());
        }

        customerDtos = customers.stream().map(customer -> {
            CustomerDto customerDto = new CustomerDto();
            BeanUtils.copyProperties(customer, customerDto);
            return customerDto;
        }).collect(Collectors.toList());
    }

    private void initMeta(boolean isAutoIncrement) throws Exception {
        Class cls = Class.forName("org.h2.Driver");
        Driver driver = (Driver) cls.newInstance();
        Connection connection = driver.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL", null);
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_customer");
        statement.execute("drop table if exists t_company");
        if (isAutoIncrement) {
            statement.execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,score bigint,company_id bigint)");
            statement.execute("create table t_company (id bigint auto_increment primary key,name varchar(20),create_time date,update_time date,`enable` int)");
        } else {
            statement.execute("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age int,create_time date,update_time date,version bigint,`enable` int,score bigint,company_id bigint)");
            statement.execute("create table t_company (id bigint primary key,name varchar(20),create_time date,update_time date,`enable` int)");
        }
        statement.closeOnCompletion();
        connection.close();
    }
}
