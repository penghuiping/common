package com.php25.common.coresample;

import com.php25.common.core.util.ReflectUtil;
import com.php25.common.coresample.dto.Customer;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(ReflectTest.class);

    @Before
    public void prepare() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("jack");
        customer.setPassword("123123");
        customer.setCreateTime(new Date());
        customer.setEnable(1);
    }

    @Test
    public void getCacheFieldJdk() {
        Class<?> cls = customer.getClass();
        Field field = ReflectUtil.getField(cls, "username");
        Assertions.assertThat(field.getName()).isEqualTo("username");
    }

    @Test
    public void getCacheMethodJdk() {
        Method method = ReflectUtil.getMethod(customer.getClass(), "getUsername");
        Assertions.assertThat(method.getName()).isEqualTo("getUsername");
    }
}
