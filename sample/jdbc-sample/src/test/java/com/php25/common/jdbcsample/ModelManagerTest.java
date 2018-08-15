package com.php25.common.jdbcsample;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.TimeUtil;
import com.php25.common.jdbc.ModelManager;
import com.php25.common.jdbcsample.model.Customer;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/10 17:50
 * @Description:
 */
public class ModelManagerTest {


    private Customer customer;

    private Integer loopSize = 10;

    @Before
    public void before() {
        customer = new Customer();
        customer.setId(1000l);
        customer.setUsername("jack" + 0);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setCreateTime(new Date());
    }

    @Test
    public void test() {
        Long start = TimeUtil.getCurrentTimeMillis();
        for (int i = 0; i < loopSize; i++) {
            ModelManager.getTableColumnNameAndValue(customer, true);
        }
        Long end = TimeUtil.getCurrentTimeMillis();
        System.out.println("test耗时为:" + (end - start));
    }
}
