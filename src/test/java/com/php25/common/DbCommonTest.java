package com.php25.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.dto.CustomerDto;
import com.php25.common.repository.CustomerRepository;
import com.php25.common.repository.impl.BaseRepositoryImpl;
import com.php25.common.service.CustomerService;
import com.php25.common.service.IdGeneratorService;
import com.php25.common.specification.Operator;
import com.php25.common.specification.SearchParam;
import com.php25.common.util.DigestUtil;
import com.php25.common.util.RandomUtil;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@DataJpaTest
@EntityScan(basePackages = {"com.php25"})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class, basePackages = {"com.php25.common.repository"})
public class DbCommonTest {

    private static final Logger logger = LoggerFactory.getLogger(DbCommonTest.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerService customerService;

    @Autowired
    IdGeneratorService idGeneratorService;

    @Autowired
    ObjectMapper objectMapper;


    @Before
    public void save() throws Exception {
        List<CustomerDto> customers = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            CustomerDto customer = new CustomerDto();
            customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setCreateTime(new Date());
            customers.add(customer);
        }
        customerService.save(customers);
    }


    @Test
    public void idGeneratorService() throws Exception {
        logger.info("snowflake:" + idGeneratorService.getModelPrimaryKeyNumber());
        logger.info("uuid:" + idGeneratorService.getModelPrimaryKey());
    }

    @Test
    public void findAll() throws Exception {
        Optional<List<CustomerDto>> customerDtos = customerService.findAll();
        customerDtos.ifPresent(a -> {
            print("<<<<<<<===========findAll===========>>>>>>", a);

            CustomerDto customerDto = a.get(RandomUtil.getRandom(0, a.size()));
            print("<<<<<<<===========findOne===========>>>>>>", customerDto);
        });
    }

    @Test
    public void delete() throws Exception {
        Optional<List<CustomerDto>> customerDtos = customerService.findAll();
        customerDtos.ifPresent(a -> {
            print("<<<<<<<===========删除前===========>>>>>>", a);
            customerService.delete(a);
        });
        customerDtos = customerService.findAll();
        customerDtos.ifPresent(a -> {
            print("<<<<<<<===========删除后===========>>>>>>", a);
        });
    }


    @Test
    public void update() throws Exception {
        Optional<List<CustomerDto>> customerDtos = customerService.findAll();
        customerDtos.ifPresent(a -> {
            print("<<<<<<<===========更新前===========>>>>>>", a);
            a.forEach(b->{
                b.setUsername("name-"+b.getUsername());
            });
            customerService.save(a);
            print("<<<<<<<===========更新后===========>>>>>>", a);
        });
    }

    @Test
    public void queryList() throws Exception {
        customerService.query(2, 1, "", Sort.Direction.ASC, "username").ifPresent(a -> {
            print("<<<<<<<===========分页查询===========>>>>>>", a.getData());

            logger.info("<<<<<<<===========分页查询in  start===========>>>>>>");
            //构建searchParams
            List<Long> ids = new ArrayList<>();
            a.getData().forEach(b -> {
                ids.add(b.getId());
            });
            SearchParam searchParam = new SearchParam();
            searchParam.setFieldName("id");
            searchParam.setOperator(Operator.NIN.name());
            try {
                searchParam.setValue(objectMapper.writeValueAsString(ids));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            try {
                String searchParams = objectMapper.writeValueAsString(Lists.newArrayList(searchParam));
                customerService.query(-1, 1, searchParams).ifPresent(c -> {
                    print("<<<<<<<===========分页查询in===========>>>>>>", c.getData());
                });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            logger.info("<<<<<<<===========分页查询in  end===========>>>>>>");
        });
    }


    private void print(String prefix, CustomerDto customer) {
        try {
            logger.info(prefix + objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void print(String prefix, List<CustomerDto> customerDtos) {
        try {
            logger.info(prefix + objectMapper.writeValueAsString(customerDtos));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
