package com.php25.common.jdbcsample;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbcsample.model.Company;
import com.php25.common.jdbcsample.model.Customer;
import com.php25.common.jdbcsample.service.CompanyService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:20
 * @Description:
 */
@SpringBootTest
@ContextConfiguration(classes = {CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class MysqlJdbcTest extends DbTest {


    @Autowired
    private CompanyService companyService;

    @Test
    public void query() {
        QueryWrapper<Customer> queryWrapper = null;
        QueryWrapper<Company> queryWrapper1 = null;

        //like
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.like("username", "jack%").orderByAsc("id").select();
        List<Customer> customers = customerMapper.selectList(queryWrapper);
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> a.getUsername().startsWith("jack")).count());

        //not like
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.notLike("username", "jack%").orderByAsc("id").select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());

        //eq
        queryWrapper1 = new QueryWrapper<Company>();
        queryWrapper1 = queryWrapper1.eq("name", "Google").select();
        Company company = companyMapper.selectOne(queryWrapper1);
        Assert.assertNotNull(company);

        //not eq
        queryWrapper1 = new QueryWrapper<Company>();
        queryWrapper1 = queryWrapper1.ne("name", "Google").select();
        company = companyMapper.selectOne(queryWrapper1);
        Assert.assertNull(company);

        //between...and..
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.between("age", 20, 50).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() >= 20 && a.getAge() <= 50).count());

        //not between...and..
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.notBetween("age", 20, 50).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() < 20 || a.getAge() > 50).count());

        //in
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.in("age", Lists.newArrayList(20, 40)).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() == 20 || a.getAge() == 40).count());

        //not in
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.notIn("age", Lists.newArrayList(0, 10)).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> (a.getAge() != 0 && a.getAge() != 10)).count());

        //great
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.gt("age", 40).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() > 40).count());

        //great equal
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.ge("age", 40).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() >= 40).count());

        //less
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.lt("age", 0).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() < 0).count());

        //less equal
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.le("age", 0).select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getAge() <= 0).count());

        //is null
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.isNull("update_time").select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getUpdateTime() == null).count());

        //is not null
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.isNotNull("update_time").select();
        customers = customerMapper.selectList(queryWrapper);
        Assert.assertEquals(customers.size(), this.customers.stream().filter(a -> a.getUpdateTime() == null).count());
    }

//    @Test
//    public void groupBy() {
//        List<Customer> customers1 = customerMapper.selectList(new QueryWrapper<Customer>().groupBy("enable").select("avg(age) as avg_age", "enable"));
//        Map<Integer, Double> result = this.customers.stream().collect(Collectors.groupingBy(Customer::getEnable, Collectors.averagingInt(Customer::getAge)));
//        System.out.println(JsonUtil.toPrettyJson(result));
//        Assert.assertTrue(null != customers1 && customers1.size() > 0);
//        for (Map map : customers1) {
//            Assert.assertTrue(BigDecimal.valueOf(result.get(map.get("enable"))).intValue() == BigDecimal.valueOf((Integer) map.get("avg_age")).intValue());
//        }
//    }

    @Test
    public void orderBy() {
        QueryWrapper<Customer> queryWrapper = null;
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.orderByDesc("age").select();
        List<Customer> customers = customerMapper.selectList(queryWrapper);

        this.customers.sort((o1, o2) -> {
            return -(o1.getAge() - o2.getAge());
        });

        Assert.assertEquals(customers.size(), this.customers.size());
        for (int i = 0; i < customers.size(); i++) {
            Assert.assertEquals(customers.get(i).getAge(), this.customers.get(i).getAge());
        }
    }

    @Test
    public void findAll() {
        QueryWrapper<Customer> queryWrapper = null;
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.select();

        List<Customer> customers = customerMapper.selectList(queryWrapper);
        Assert.assertNotNull(customers);
        Assert.assertEquals(customers.size(), this.customers.size());
    }

    @Test
    public void findOne() {
        QueryWrapper<Customer> queryWrapper = null;
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.eq("username", "jack0").select();
        Customer customer = customerMapper.selectOne(queryWrapper);
        Assert.assertTrue(null != customer && "jack0".equals(customer.getUsername()));
    }

    @Test
    public void count() {
        QueryWrapper<Customer> queryWrapper = null;
        queryWrapper = new QueryWrapper<Customer>();
        queryWrapper = queryWrapper.eq("enable", "1");

        Integer count = customerMapper.selectCount(queryWrapper);
        Assert.assertEquals(this.customers.stream().filter(a -> a.getEnable() == 1).count(), (long) count);
    }

    @Test
    public void insert() throws Exception {
        customerMapper.delete(new QueryWrapper<Customer>());
        companyMapper.delete(new QueryWrapper<Company>());

        Company company = new Company();
        company.setName("test");
        company.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
        company.setCreateTime(new Date());
        company.setEnable(1);


        Customer customer = new Customer();
        if (!isAutoIncrement)
            customer.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
        customer.setUsername("mary");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(new Date());
        customer.setScore(BigDecimal.valueOf(1000L));
        customer.setEnable(1);
        customer.setCompanyId(company.getId());
        customerMapper.insert(customer);

        Customer customer1 = new Customer();
        if (!isAutoIncrement)
            customer1.setId(idGeneratorService.getModelPrimaryKeyNumber().longValue());
        customer1.setUsername("perter");
        customer1.setPassword(DigestUtil.MD5Str("123456"));
        customer1.setAge(10);
        customer1.setStartTime(new Date());
        customer1.setScore(BigDecimal.valueOf(1000L));
        customer1.setEnable(1);
        customer1.setCompanyId(company.getId());
        customerMapper.insert(customer1);
        companyMapper.insert(company);

        Assert.assertEquals(2, customerMapper.selectCount(new QueryWrapper<>()).intValue());
        Assert.assertEquals(1, companyMapper.selectCount(new QueryWrapper<>()).intValue());

    }

//    @Test
//    public void batchUpdate() {
//        List<Customer> customers = db.cnd(Customer.class).select();
//        customers = customers.stream().map(a -> {
//            a.setUsername(a.getUsername().replace("jack", "tom"));
//            return a;
//        }).collect(Collectors.toList());
//        int[] arr = db.cnd(Customer.class).updateBatch(customers);
//        for (int e : arr) {
//            Assert.assertEquals(e, 1);
//        }
//    }

    @Test
    public void update() {
        Customer customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("username", "jack0"));
        customer.setUsername("jack-0");
        customerMapper.updateById(customer);
        customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("username", "jack0"));
        Assert.assertNull(customer);
        customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("username", "jack-0"));
        Assert.assertNotNull(customer);
    }

    @Test
    public void updateVersion() throws Exception {
        CountDownLatch countDownLatch1 = new CountDownLatch(100);
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        AtomicInteger atomicInteger = new AtomicInteger();
        List<Callable<Object>> runnables = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            runnables.add(() -> {
                Customer customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("username", "jack0"));
                customer.setScore(customer.getScore().subtract(BigDecimal.valueOf(1)));
                int rows = customerMapper.updateById(customer);
                if (rows > 0) {
                    atomicInteger.addAndGet(1);
                }
                countDownLatch1.countDown();
                return true;
            });
        }
        executorService.invokeAll(runnables);

        countDownLatch1.await();
        System.out.println("===========>更新成功的数量:" + atomicInteger.get());
        Customer customer = customerMapper.selectOne(new QueryWrapper<Customer>().eq("username", "jack0"));
        Assert.assertTrue(1000L - customer.getScore().longValue() == atomicInteger.get() && atomicInteger.get() == customer.getVersion());
    }

    @Test
    public void delete() {
        customerMapper.delete(new QueryWrapper<Customer>().like("username", "jack%"));
        List<Customer> customers = customerMapper.selectList(new QueryWrapper<Customer>());
        Assert.assertTrue(customers != null && customers.size() == this.customers.stream().filter(a -> !a.getUsername().startsWith("jack")).count());
    }

    @Test
    public void queryByName() {
        Company company = companyMapper.queryByName("Google");
        System.out.println(JsonUtil.toPrettyJson(company));

        List<Map> map = companyMapper.join("Google");
        System.out.println(JsonUtil.toPrettyJson(map));
    }

    @Test
    public void test123() {
        Company company = companyService.getOne(new QueryWrapper<Company>().eq("name", "Google"));
        System.out.println(JsonUtil.toPrettyJson(company));
        company.setName("BaiDu");
        companyService.saveOrUpdate(company);
        company = companyService.getOne(new QueryWrapper<Company>().eq("name", "BaiDu"));
        System.out.println(JsonUtil.toPrettyJson(company));
    }


}
