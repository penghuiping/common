package com.php25.common.jdbcsample.oracle.test;

import com.google.common.collect.Lists;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.specification.Operator;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;
import com.php25.common.jdbcsample.oracle.CommonAutoConfigure;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import com.php25.common.jdbcsample.oracle.model.Department;
import com.php25.common.jdbcsample.oracle.model.DepartmentRef;
import com.php25.common.jdbcsample.oracle.repository.CompanyRepository;
import com.php25.common.jdbcsample.oracle.repository.CustomerRepository;
import com.php25.common.jdbcsample.oracle.repository.DepartmentRefRepository;
import com.php25.common.jdbcsample.oracle.repository.DepartmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.AbstractWaitStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2021/1/15 13:38
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class OracleRepositoryTest extends DbTest {

    @ClassRule
    public static GenericContainer oracle = new GenericContainer<>("wnameless/oracle-xe-11g-r2")
            .withExposedPorts(1521)
            .waitingFor(new AbstractWaitStrategy() {
                @Override
                protected void waitUntilReady() {
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

    static {
        oracle.setPortBindings(Lists.newArrayList("1521:1521"));
        oracle.withEnv("ORACLE_ALLOW_REMOTE", "true");
        oracle.withEnv("ORACLE_DISABLE_ASYNCH_IO", "true");
    }

    /**
     * repository test
     */
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DepartmentRefRepository departmentRefRepository;

    @Test
    public void findAllEnabled() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assertions.assertThat(customers.size()).isEqualTo(this.customers.stream().filter(a -> a.getEnable() == 1).count());
    }

    @Test
    public void findAllSort() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder().append(Lists.newArrayList());
        Iterable<Customer> customers = customerRepository.findAll(searchParamBuilder, Sort.by(Sort.Order.desc("id")));
        Assertions.assertThat(Lists.newArrayList(customers).size()).isEqualTo(this.customers.size());
        Assertions.assertThat(Lists.newArrayList(customers).get(0).getId()).isEqualTo(this.customers.get(this.customers.size() - 1).getId());
    }

    @Test
    public void findAllPage() {
        SearchParamBuilder searchParamBuilder = SearchParamBuilder.builder().append(Lists.newArrayList());
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        Page<Customer> customers = customerRepository.findAll(searchParamBuilder, page);
        Assertions.assertThat(customers.getContent().size()).isEqualTo(2);
    }

    @Test
    public void save() {
        //新增
        Company company = new Company();
        company.setId(snowflakeIdWorker.nextId());
        company.setName("baidu");
        company.setEnable(1);
        company.setCreateTime(new Date());
        company.setNew(true);
        companyRepository.save(company);
        SearchParamBuilder builder = SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "baidu"));
        Assertions.assertThat(companyRepository.findOne(builder).get().getName()).isEqualTo("baidu");

        Customer customer = new Customer();
        customer.setUsername("jack" + 4);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(4 * 10);
        customer.setCompanyId(company.getId());
        customer.setNew(true);
        customerRepository.save(customer);
        builder = SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack4"));
        Assertions.assertThat(customerRepository.findOne(builder).get().getUsername()).isEqualTo("jack4");

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
        Assertions.assertThat(customerOptional.get().getUsername()).isEqualTo("jack" + 5);
    }

    @Test
    public void saveAll() {
        customerRepository.deleteAll();
        //保存
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
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
        Assertions.assertThat(Lists.newArrayList(customerRepository.findAll()).stream().filter(customer -> customer.getUsername().startsWith("mary")).count()).isEqualTo(3);
    }

    @Test
    public void findById() {
        Customer customer = customers.get(0);
        Optional<Customer> customer1 = customerRepository.findById(customer.getId());
        Assertions.assertThat(customer1.get().getId()).isEqualTo(customer.getId());
    }

    @Test
    public void existsById() {
        Customer customer = customers.get(0);
        Boolean result = customerRepository.existsById(customer.getId());
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void _findAll() {
        Iterable iterable = customerRepository.findAll();
        Assertions.assertThat(Lists.newArrayList(iterable).size()).isEqualTo(customers.size());
    }

    @Test
    public void findAllById() {
        List<Long> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        Iterable iterable = customerRepository.findAllById(ids);
        Assertions.assertThat(Lists.newArrayList(iterable).size()).isEqualTo(customers.size());
    }

    @Test
    public void _count() {
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size());
    }

    @Test
    public void deleteById() {
        customerRepository.deleteById(customers.get(0).getId());
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size() - 1);
        Assertions.assertThat(customerRepository.existsById(customers.get(0).getId())).isFalse();
    }

    @Test
    public void deleteByModel() {
        customerRepository.delete(customers.get(1));
        Assertions.assertThat(customerRepository.count()).isEqualTo(customers.size() - 1);
        Assertions.assertThat(customerRepository.existsById(customers.get(1).getId())).isFalse();
    }

    @Test
    public void deleteAllByIds() {
        Iterable<Customer> ids = customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList());
        customerRepository.deleteAll(ids);
        customers.stream().filter(customer -> customer.getId() % 2 == 0).collect(Collectors.toList()).forEach(customer -> {
            Assertions.assertThat(customerRepository.existsById(customer.getId())).isFalse();
        });
    }

    @Test
    public void deleteAll() {
        customerRepository.deleteAll();
        Assertions.assertThat(customerRepository.count()).isEqualTo(0);
    }

    @Test
    public void testManyToMany0() {
        //新增操作
        //部门
        Department department = new Department();
        department.setId(snowflakeIdWorker.nextId());
        department.setName("testDepart");
        department.setNew(true);
        department = departmentRepository.save(department);

        Department department1 = new Department();
        department1.setId(snowflakeIdWorker.nextId());
        department1.setName("testDepart1");
        department1.setNew(true);
        department1 = departmentRepository.save(department1);

        //人员
        Customer customer = new Customer();
        customer.setUsername("jack12313");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setStartTime(LocalDateTime.now());
        customer.setAge(10);
        customer.setNew(true);
        customer.setEnable(1);
        customer = customerRepository.save(customer);
        Assertions.assertThat(customer.getId()).isNotNull();

        //部门与人员关系
        DepartmentRef departmentRef = new DepartmentRef();
        departmentRef.setDepartmentId(department.getId());
        departmentRef.setCustomerId(customer.getId());
        departmentRefRepository.deleteByCustomerIds(Lists.newArrayList(customer.getId()));
        departmentRefRepository.save(Lists.newArrayList(departmentRef));

        Department _department = departmentRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("name", Operator.EQ, "testDepart"))).get();
        Assertions.assertThat(department.getId()).isEqualTo(_department.getId());

        Customer _customer = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313"))).get();
        Assertions.assertThat(_customer.getId()).isEqualTo(customer.getId());
        List<DepartmentRef> departmentRefs = departmentRefRepository.findByCustomerId(_customer.getId());
        Assertions.assertThat(departmentRefs.size()).isEqualTo(1);

        //更新操作
        //更新部门与人员关系
        DepartmentRef departmentRef1 = new DepartmentRef();
        departmentRef1.setDepartmentId(department1.getId());
        departmentRef1.setCustomerId(customer.getId());
        departmentRefRepository.deleteByCustomerIds(Lists.newArrayList(customer.getId()));
        departmentRefRepository.save(Lists.newArrayList(departmentRef, departmentRef1));

        _customer = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313"))).get();
        departmentRefs = departmentRefRepository.findByCustomerId(_customer.getId());
        Assertions.assertThat(departmentRefs.size()).isEqualTo(2);

        //删除操作
        customerRepository.delete(customer);
        Optional optional = customerRepository.findOne(SearchParamBuilder.builder().append(SearchParam.of("username", Operator.EQ, "jack12313")));
        Assertions.assertThat(optional.isPresent()).isEqualTo(false);
    }

    @Test
    public void testManyToMany1() {
        List<Customer> customers = customerRepository.findAllEnabled();
        Assertions.assertThat(customers.size()).isEqualTo(3);

        Customer customer1 = customers.stream().filter(customer -> customer.getUsername().equals("jack0")).findAny().get();
        //部门
        Department department = new Department();
        department.setId(snowflakeIdWorker.nextId());
        department.setName("testDepart");
        department.setNew(true);

        Department department1 = new Department();
        department1.setId(snowflakeIdWorker.nextId());
        department1.setName("testDepart1");
        department1.setNew(true);
        departmentRepository.saveAll(Lists.newArrayList(department, department1));

        DepartmentRef departmentRef = new DepartmentRef();
        departmentRef.setDepartmentId(department.getId());
        departmentRef.setCustomerId(customer1.getId());

        DepartmentRef departmentRef1 = new DepartmentRef();
        departmentRef1.setDepartmentId(department1.getId());
        departmentRef1.setCustomerId(customer1.getId());

        departmentRefRepository.save(Lists.newArrayList(departmentRef, departmentRef1));
        List<DepartmentRef> departmentRefs = departmentRefRepository.findByCustomerId(customer1.getId());
        Assertions.assertThat(departmentRefs.size()).isEqualTo(2);
        departmentRefs.forEach(departmentRef2 -> {
            Assertions.assertThat(departmentRef2.getCustomerId()).isNotNull();
            Assertions.assertThat(departmentRef2.getDepartmentId()).isNotNull();
        });

        departmentRefRepository.deleteByCustomerIds(Lists.newArrayList(customer1.getId()));
        departmentRefs = departmentRefRepository.findByCustomerId(customer1.getId());
        Assertions.assertThat(departmentRefs).isNullOrEmpty();
    }
}
