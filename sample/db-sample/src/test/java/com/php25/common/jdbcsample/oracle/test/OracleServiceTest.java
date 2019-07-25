package com.php25.common.jdbcsample.oracle.test;

import com.google.common.collect.Lists;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.jdbcsample.oracle.CommonAutoConfigure;
import com.php25.common.jdbcsample.oracle.dto.CustomerDto;
import com.php25.common.jdbcsample.oracle.model.Customer;
import com.php25.common.jdbcsample.oracle.service.CustomerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/17 13:53
 * @Description:
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class OracleServiceTest extends DbTest {

    private static final Logger log = LoggerFactory.getLogger(OracleServiceTest.class);
    @Autowired
    CustomerService customerService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    protected void initDb() {
        this.db = new Db(jdbcTemplate, DbType.ORACLE);
    }


    @Test
    public void findOne() {
        Optional<CustomerDto> customerDto = customerService.findOne(customers.get(0).getId());
        Assert.assertEquals(customerDto.get().getId(), customers.get(0).getId());
    }

    @Test
    public void findOne1() {
        Optional<CustomerDto> customerDto = customerService.findOne(customers.get(0).getId(), BeanUtils::copyProperties);
        Assert.assertEquals(customerDto.get().getId(), customers.get(0).getId());
    }

    @Test
    public void save() {
        CustomerDto customer = new CustomerDto();
        if (!isSequence)
            customer.setId(10L);
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());
        customerService.save(customer);
        Customer tmp = db.cndJpa(Customer.class).andEq("username", "jack10").single();
        Assert.assertEquals(tmp.getUsername(), customer.getUsername());
    }

    @Test
    public void save1() {
        CustomerDto customer = new CustomerDto();
        if (!isSequence)
            customer.setId(10L);
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());
        customerService.save(customer, BeanUtils::copyProperties, BeanUtils::copyProperties);
        Customer tmp = db.cndJpa(Customer.class).andEq("username", "jack10").single();
        Assert.assertEquals(tmp.getUsername(), customer.getUsername());
    }

    @Test
    public void save2() {
        CustomerDto customer = new CustomerDto();
        if (!isSequence)
            customer.setId(10L);
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());

        CustomerDto customer1 = new CustomerDto();
        if (!isSequence)
            customer1.setId(11L);
        customer1.setUsername("jack" + 11);
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(11 * 10);
        customer1.setCreateTime(new Date());
        customerService.save(Lists.newArrayList(customer, customer1));

        Customer tmp = db.cndJpa(Customer.class).andEq("username", "jack10").single();
        Customer tmp1 = db.cndJpa(Customer.class).andEq("username", "jack11").single();
        Assert.assertEquals(tmp.getUsername(), customer.getUsername());
        Assert.assertEquals(tmp1.getUsername(), customer1.getUsername());

    }

    @Test
    public void save3() {
        CustomerDto customer = new CustomerDto();
        if (!isSequence)
            customer.setId(10L);
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());

        CustomerDto customer1 = new CustomerDto();
        if (!isSequence)
            customer1.setId(11L);
        customer1.setUsername("jack" + 11);
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(11 * 10);
        customer1.setCreateTime(new Date());

        customerService.save(Lists.newArrayList(customer, customer1), BeanUtils::copyProperties);

        Customer tmp = db.cndJpa(Customer.class).andEq("username", "jack10").single();
        Customer tmp1 = db.cndJpa(Customer.class).andEq("username", "jack11").single();
        Assert.assertEquals(tmp.getUsername(), customer.getUsername());
        Assert.assertEquals(tmp1.getUsername(), customer1.getUsername());
    }

    @Test
    public void delete() {
        customerService.delete(customerDtos.get(0));
        Optional<CustomerDto> customerDtoOptional = customerService.findOne(customers.get(0).getId());
        Assert.assertFalse(customerDtoOptional.isPresent());
    }

    @Test
    public void delete1() {
        customerService.delete(Lists.newArrayList(customerDtos.get(0), customerDtos.get(2)));

        Optional<CustomerDto> customerDtoOptional0 = customerService.findOne(customers.get(0).getId());
        Optional<CustomerDto> customerDtoOptional1 = customerService.findOne(customers.get(2).getId());
        Assert.assertFalse(customerDtoOptional0.isPresent());
        Assert.assertFalse(customerDtoOptional1.isPresent());
    }

    @Test
    public void findAll() {
        Optional<List<CustomerDto>> customerDtoOptional = customerService.findAll(Lists.newArrayList(customers.get(0).getId(), customers.get(2).getId()));
        Assert.assertTrue(customerDtoOptional.isPresent() && customerDtoOptional.get().size() == 2);
    }

    @Test
    public void findAll1() {
        Optional<List<CustomerDto>> customerDtoOptional = customerService.findAll(Lists.newArrayList(customers.get(0).getId(), customers.get(2).getId()), BeanUtils::copyProperties);
        Assert.assertTrue(customerDtoOptional.isPresent() && customerDtoOptional.get().size() == 2);
    }

    @Test
    public void findAll2() {
        Optional<List<CustomerDto>> listOptional = customerService.findAll();
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().size() == customers.size());
    }

    @Test
    public void findAll3() {
        Optional<List<CustomerDto>> listOptional = customerService.findAll((customer, customerDto) -> BeanUtils.copyProperties(customer, customerDto));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().size() == customers.size());
    }

    @Test
    public void query() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    @Test
    public void query1() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), Sort.Direction.DESC, "id");
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    public void query2() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), BeanUtils::copyProperties, Sort.Direction.DESC, "id");
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    @Test
    public void query3() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), BeanUtils::copyProperties, Sort.by(Sort.Order.desc("id")));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    @Test
    public void query4() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, searchParamBuilder, BeanUtils::copyProperties, Sort.by(Sort.Order.desc("id")));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    @Test
    public void count() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).value("jack1").build());
        long count = customerService.count(JsonUtil.toJson(searchParamBuilder.build()));
        Assert.assertTrue(count == 1);
    }
}
