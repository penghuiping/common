package com.php25.common.jdbcsample;

import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbc.Db;
import com.php25.common.jdbcsample.dto.CustomerDto;
import com.php25.common.jdbcsample.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
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
public class ServiceTest {

    @Autowired
    IdGeneratorService idGeneratorService;

    @Autowired
    CustomerService customerService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Db db;

    Logger log = LoggerFactory.getLogger(RepositoryTest.class);

    List<CustomerDto> customers;


    @Before
    public void before() {
        jdbcTemplate.batchUpdate("drop table if exists t_customer", "create table t_customer (id bigint,username varchar(20),password varchar(50),age int,create_time date,update_time date,`enable` bit)");

        customers = Lists.newArrayList();
        for (int i = 0; i <= 3; i++) {
            CustomerDto customer = new CustomerDto();
            customer.setId(new Long(i));
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setCreateTime(new Date());
            if (i % 2 == 0)
                customer.setEnable(1);
            else
                customer.setEnable(0);
            customers.add(customer);
        }
        customerService.save(customers);
    }

    /**
     * 根据id查找
     *
     * @param id
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findOne() {
        Optional<CustomerDto> customerDto = customerService.findOne(customers.get(0).getId());
        Assert.assertEquals(customerDto.get().getId(), customers.get(0).getId());

    }

    /**
     * 根据id查找
     *
     * @param id
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findOne1() {
        Optional<CustomerDto> customerDto = customerService.findOne(customers.get(0).getId(), BeanUtils::copyProperties);
        Assert.assertEquals(customerDto.get().getId(), customers.get(0).getId());
    }

    /**
     * 保存或者更新
     *
     * @param obj
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void save() {
        CustomerDto customer = new CustomerDto();
        customer.setId(new Long(10));
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());
        customerService.save(customer);
        Assert.assertEquals(customerService.findOne(customer.getId()).get().getId(), customer.getId());
    }

    /**
     * 保存或者更新
     *
     * @param obj
     * @param dtoToModelTransferable
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void save1() {
        CustomerDto customer = new CustomerDto();
        customer.setId(new Long(10));
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());
        customerService.save(customer, BeanUtils::copyProperties, BeanUtils::copyProperties);
        log.info(JsonUtil.toPrettyJson(customerService.findAll().get()));
    }

    /**
     * 保存或者更新批量
     *
     * @param objs
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void save2() {
        CustomerDto customer = new CustomerDto();
        customer.setId(new Long(10));
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());

        CustomerDto customer1 = new CustomerDto();
        customer1.setId(new Long(11));
        customer1.setUsername("jack" + 11);
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(11 * 10);
        customer1.setCreateTime(new Date());
        customerService.save(Lists.newArrayList(customer, customer1));

        Assert.assertEquals(customerService.findOne(customer.getId()).get().getId(), customer.getId());
        Assert.assertEquals(customerService.findOne(customer1.getId()).get().getId(), customer1.getId());
    }

    /**
     * 保存或者更新批量
     *
     * @param objs
     * @param dtoToModelTransferable
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void save3() {
        CustomerDto customer = new CustomerDto();
        customer.setId(new Long(10));
        customer.setUsername("jack" + 10);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10 * 10);
        customer.setCreateTime(new Date());

        CustomerDto customer1 = new CustomerDto();
        customer1.setId(new Long(11));
        customer1.setUsername("jack" + 11);
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(11 * 10);
        customer1.setCreateTime(new Date());

        customerService.save(Lists.newArrayList(customer, customer1), BeanUtils::copyProperties);

        Assert.assertEquals(customerService.findOne(customer.getId()).get().getId(), customer.getId());
        Assert.assertEquals(customerService.findOne(customer1.getId()).get().getId(), customer1.getId());
    }

    /**
     * 物理删除
     *
     * @param obj
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void delete() {
        customerService.delete(customers.get(0));
        Optional<CustomerDto> customerDtoOptional = customerService.findOne(customers.get(0).getId());
        Assert.assertFalse(customerDtoOptional.isPresent());
    }

    /**
     * 批量物理删除
     *
     * @param objs
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void delete1() {
        customerService.delete(Lists.newArrayList(customers.get(0), customers.get(2)));

        Optional<CustomerDto> customerDtoOptional0 = customerService.findOne(customers.get(0).getId());
        Optional<CustomerDto> customerDtoOptional1 = customerService.findOne(customers.get(2).getId());
        Assert.assertFalse(customerDtoOptional0.isPresent());
        Assert.assertFalse(customerDtoOptional1.isPresent());
    }

    /**
     * 根据id查找
     *
     * @param ids
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findAll() {
        Optional<List<CustomerDto>> customerDtoOptional = customerService.findAll(Lists.newArrayList(customers.get(0).getId(), customers.get(2).getId()));
        Assert.assertTrue(customerDtoOptional.isPresent() && customerDtoOptional.get().size() == 2);
    }

    /**
     * 根据id查找
     *
     * @param modelToDtoTransferable
     * @param ids
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findAll1() {
        Optional<List<CustomerDto>> customerDtoOptional = customerService.findAll(Lists.newArrayList(customers.get(0).getId(), customers.get(2).getId()), BeanUtils::copyProperties);
        Assert.assertTrue(customerDtoOptional.isPresent() && customerDtoOptional.get().size() == 2);
    }

    /**
     * 查找所有的
     *
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findAll2() {
        Optional<List<CustomerDto>> listOptional = customerService.findAll();
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().size() == customers.size());
    }


    /**
     * 查找所有的
     *
     * @param modelToDtoTransferable
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void findAll3() {
        Optional<List<CustomerDto>> listOptional = customerService.findAll((customer, customerDto) -> BeanUtils.copyProperties(customer, customerDto));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().size() == customers.size());
    }

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void query() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param direction
     * @param property
     * @return
     */
    @Test
    public void query1() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), Sort.Direction.DESC, "id");
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param modelToDtoTransferable
     * @param direction
     * @param property
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    public void query2() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), BeanUtils::copyProperties, Sort.Direction.DESC, "id");
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParams
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    @Test
    public void query3() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, JsonUtil.toJson(searchParamBuilder.build()), BeanUtils::copyProperties, Sort.by(Sort.Order.desc("id")));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    /**
     * 分页条件筛选查找
     *
     * @param pageNum
     * @param pageSize
     * @param searchParamBuilder
     * @param customerModelToDtoTransferable
     * @param sort
     * @return
     */
    @Test
    public void query4() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.LIKE).value("jack%").build());
        Optional<DataGridPageDto<CustomerDto>> listOptional = customerService.query(1, 2, searchParamBuilder, BeanUtils::copyProperties, Sort.by(Sort.Order.desc("id")));
        Assert.assertTrue(listOptional.isPresent() && listOptional.get().getData().size() == 2);
    }

    /**
     * 筛选计算数量
     *
     * @param searchParams
     * @return
     * @author penghuiping
     * @date 16/8/12.
     */
    @Test
    public void count() {
        SearchParamBuilder searchParamBuilder = new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).value("jack1").build());
        long count = customerService.count(JsonUtil.toJson(searchParamBuilder.build()));
        Assert.assertTrue(count == 1);
    }
}
