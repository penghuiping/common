package com.php25.common.jpasample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.dto.DataGridPageDto;
import com.php25.common.core.service.IdGeneratorService;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.core.specification.Operator;
import com.php25.common.core.specification.SearchParam;
import com.php25.common.core.specification.SearchParamBuilder;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.core.util.RandomUtil;
import com.php25.common.jpa.repository.BaseRepositoryImpl;
import com.php25.common.jpasample.dto.CustomerDto;
import com.php25.common.jpasample.repository.CustomerRepository;
import com.php25.common.jpasample.service.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
@EntityScan(basePackages = {"com.php25"})
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class JpaTest {

    private static final Logger logger = LoggerFactory.getLogger(JpaTest.class);
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerService customerService;
    @Autowired
    IdGeneratorService idGeneratorService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Before
    public void save() throws Exception {
        List<CustomerDto> customers = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            CustomerDto customer = new CustomerDto();
            customer.setId(snowflakeIdWorker.nextId());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setCreateTime(new Date());
            customer.setEnable(1);
            customers.add(customer);
        }
        customerService.save(customers);
    }


    @Test
    public void findAll() throws Exception {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            Optional<List<CustomerDto>> customerDtos = customerService.findAll();
            if (customerDtos.isPresent()) {
                List<CustomerDto> a = customerDtos.get();
                print("<<<<<<<===========findAll===========>>>>>>", a);

                CustomerDto customerDto = a.get(RandomUtil.getRandom(0, a.size()));
                print("<<<<<<<===========findOne===========>>>>>>", customerDto);
            }
        }
        logger.info("耗时:{}ms", System.currentTimeMillis() - startTime);
    }

    @Test
    public void findAllAsync() throws Exception {
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 1; i++) {
            Mono<Optional<List<CustomerDto>>> mono = customerService.findAllAsync();
            mono.subscribe(customerDtos1 -> {
                if (customerDtos1.isPresent() && !customerDtos1.get().isEmpty()) {
                    List<CustomerDto> a = customerDtos1.get();
                    print("<<<<<<<===========findAll===========>>>>>>", a);

                    CustomerDto customerDto = a.get(RandomUtil.getRandom(0, a.size()));
                    print("<<<<<<<===========findOne===========>>>>>>", customerDto);
                }
            }, throwable -> {
                logger.error("出错啦", throwable);
            }, () -> {
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        logger.info("耗时:{}ms", System.currentTimeMillis() - startTime);

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
    public void softDelete() throws Exception {
        Optional<List<CustomerDto>> customerDtos = customerService.findAll();
        customerDtos.ifPresent(a -> {
            print("<<<<<<<===========删除前===========>>>>>>", a);
            customerService.softDelete(a);
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
            a.forEach(b -> {
                b.setUsername("name-" + b.getUsername());
            });
            customerService.save(a);
            print("<<<<<<<===========更新后===========>>>>>>", a);
        });
    }

    @Test
    public void queryList() throws Exception {
        customerService.query(2, 1, "[]", Sort.Direction.ASC, "username").ifPresent(a -> {
            print("<<<<<<<===========分页查询===========>>>>>>", a.getData());

            logger.info("<<<<<<<===========分页查询in  start===========>>>>>>");
            //构建searchParams
            List<Long> ids = new ArrayList<>();
            a.getData().forEach(b -> {
                ids.add(b.getId());
            });
            SearchParam searchParam = null;

            try {
                searchParam = new SearchParam.Builder().fieldName("id").operator(Operator.NIN).value(objectMapper.writeValueAsString(ids)).build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            SearchParamBuilder searchParamBuilder = new SearchParamBuilder();
            searchParamBuilder.append(searchParam);

            Sort sort = Sort.by(Sort.Direction.DESC, "id");

            customerService.query(1, 10, searchParamBuilder, (customer, customerDto) -> BeanUtils.copyProperties(customer, customerDto), sort).ifPresent(c -> {
                print("<<<<<<<===========分页查询in===========>>>>>>", c.getData());
                List sorted = c.getData().stream().sorted((o1, o2) -> {
                    return -(int) (o1.getId() - o2.getId());
                }).collect(Collectors.toList());
                print("<<<<<<<===========分页查询in===========>>>>>>", sorted);
            });

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

    @Test
    public void testt() {
        Optional<DataGridPageDto<CustomerDto>> customerDtos = customerService.query(1, 1, new SearchParamBuilder().append(new SearchParam.Builder().fieldName("username").operator(Operator.EQ).value("jack0").build()), BeanUtils::copyProperties, Sort.by(Sort.Order.asc("id")));
        System.out.println(JsonUtil.toPrettyJson(customerDtos.get().getData()));
    }

}
