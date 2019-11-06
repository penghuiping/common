package com.php25.common.jdbcsample.oracle.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.jdbcsample.oracle.CommonAutoConfigure;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import com.php25.common.jdbcsample.oracle.repository.CompanyRepository;
import com.php25.common.jdbcsample.oracle.repository.CustomerRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class OracleRepositoryTest extends DbTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UidGenerator uidGenerator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    protected void initDb() {
        this.db = new Db(jdbcTemplate, DbType.ORACLE);
    }

    @Test
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getEnable() == 1).count());
    }

    @Test
    public void findAllSort() {
        Iterable<Customer> customers = customerRepository.findAll(SearchParamBuilder.builder(), Sort.by(Sort.Order.desc("id")));
        Assert.assertEquals(Lists.newArrayList(customers).size(), this.customers.size());
        Assert.assertEquals(Lists.newArrayList(customers).get(0).getId(), this.customers.get(this.customers.size() - 1).getId());
    }

    @Test
    public void findAllPage() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(SearchParamBuilder.builder(), page);
        Assert.assertEquals(customers.getContent().size(), 2);
    }

    @Test
    public void save() {
        //新增
        Company company = new Company();
        //company.setId(idGeneratorService.getSnowflakeId().longValue());
        company.setName("baidu");
        company.setEnable(1);
        company.setCreateTime(new Date());
        company.setNew(true);
        companyRepository.save(company);
        SearchParamBuilder builder = SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "baidu"));
        Assert.assertEquals(companyRepository.findOne(builder).get().getName(), "baidu");

        Customer customer = new Customer();
        if (!isSequence)
            customer.setId(uidGenerator.getUID());
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(4 * 10);
        customer.setCompanyId(company.getId());
        customer.setNew(true);
        customerRepository.save(customer);
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Assert.assertEquals(customerRepository.findOne(builder).get().getUsername(), "jack4");

        //更新
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Optional<Customer> customerOptional = customerRepository.findOne(builder);
        customer = customerOptional.get();
        customer.setUsername("jack" + 5);
        customer.setUpdateTime(LocalDateTime.now());
        customer.setNew(false);
        customerRepository.save(customer);

        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack5"));
        customerOptional = customerRepository.findOne(builder);
        Assert.assertEquals(customerOptional.get().getUsername(), "jack" + 5);
    }

    @Test
    public void saveAll() {
        customerRepository.deleteAll();
        //保存
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            if (!isSequence)
                customer.setId(uidGenerator.getUID());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge((i + 1) * 10);
            customer.setNew(true);
            customers.add(customer);
        }
        customerRepository.saveAll(customers);
        Assert.assertEquals(Lists.newArrayList(customerRepository.findAll()).size(), 3);

        //更新
        customers = Lists.newArrayList(customerRepository.findAll());
        for (int i = 0; i < 3; i++) {
            customers.get(i).setUsername("mary" + i);
            customers.get(i).setNew(false);
        }
        customerRepository.saveAll(customers);

        System.out.println(JsonUtil.toPrettyJson(customerRepository.findAll()));
    }

    @Test
    public void findById() {
        Customer customer = customers.get(0);
        Optional<Customer> customer1 = customerRepository.findById(customer.getId());
        Assert.assertEquals(customer1.get().getId(), customer.getId());
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
        Assert.assertEquals(customerRepository.count(), customers.size() - 1);
        Assert.assertFalse(customerRepository.existsById(customers.get(0).getId()));
    }

    @Test
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
        Assert.assertEquals(customerRepository.count(), customers.size() - 1);
        Assert.assertFalse(customerRepository.existsById(customers.get(1).getId()));
    }

    @Test
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
        customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList()).forEach(customer -> {
            Assert.assertFalse(customerRepository.existsById(customer.getId()));
        });
    }

    @Test
    public void deleteAll() {
        customerRepository.deleteAll();
        Assert.assertEquals(customerRepository.count(), 0);
    }
}
