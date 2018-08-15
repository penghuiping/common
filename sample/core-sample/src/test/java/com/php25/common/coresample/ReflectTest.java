package com.php25.common.coresample;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.core.util.TimeUtil;
import com.php25.common.coresample.model.Customer;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;


/**
 * @Auther: penghuiping
 * @Date: 2018/8/9 13:49
 * @Description:
 */
public class ReflectTest {

    private Customer customer;

    private Long loopSize = 10l;

    @Before
    public void prepare() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("jack");
        customer.setPassword("123123");
        customer.setCreateTime(new Date());
        customer.setEnable(1);
    }

    // @Test
    public void test() {
        Field[] fields = Customer.class.getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                primaryKeyField = field;
                break;
            }
        }
        if (null == primaryKeyField) throw new RuntimeException("此类没有用@Id主键");
        System.out.println(primaryKeyField.getName());
    }

    //@Test
    public void get() {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            customer.getUsername();
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("get耗时为:" + (end - start));
    }

    //@Test
    public void reflectGet() throws Exception {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            customer.getClass().getDeclaredMethod("getUsername").invoke(customer);
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("reflectGet耗时为:" + (end - start));
    }


    //@Test
    public void cacheReflectGet() throws Exception {
        Method method = customer.getClass().getDeclaredMethod("getUsername");
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            method.invoke(customer);
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("cacheReflectGet耗时为:" + (end - start));
    }

    //@Test
    public void getFieldJdk() throws Exception {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            Field field = customer.getClass().getDeclaredField("username");
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("getFieldJdk耗时为:" + (end - start));
    }

    //@Test
    public void getCacheFieldJdk() throws Exception {
        Class cls = customer.getClass();
        Long start = TimeUtil.getCurrentTimeMillis();

        for (int i = 0; i < loopSize; i++) {
            ReflectUtil.getField(cls, "username");
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("getCacheFieldJdk耗时为:" + (end - start));
    }


    @Test
    public void getMethodJdk() throws Exception {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            Method method = customer.getClass().getDeclaredMethod("getUsername");
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("getMethodJdk耗时为:" + (end - start));
    }

    @Test
    public void getCacheMethodJdk() throws Exception {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            Method method = ReflectUtil.getMethod(customer.getClass(), "getUsername");
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("getCacheMethodJdk耗时为:" + (end - start));
    }


}
