package com.php25.common.jdbcsample;

import com.php25.common.core.util.DigestUtil;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.jdbc.JpaModelManager;
import com.php25.common.jdbcsample.model.Customer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/10 17:50
 * @Description:
 */
public class ModelManagerTest {

    private Logger logger = LoggerFactory.getLogger(ModelManagerTest.class);

    private Customer customer;

    @Before
    public void before() {
        customer = new Customer();
        customer.setId(1000l);
        customer.setUsername("jack" + 0);
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setCreateTime(new Date());
    }

    @Test
    public void getTableColumnNameAndValue() {
        List<ImmutablePair<String, Object>> immutablePairs = JpaModelManager.getTableColumnNameAndValue(customer, true);
        logger.info(JsonUtil.toPrettyJson(immutablePairs));
    }

    @Test
    public void getDbColumnByClassColumn() {
        String value = JpaModelManager.getDbColumnByClassColumn(Customer.class, "createTime");
        logger.info(value);
    }

    @Test
    public void test() {
        //logger.info();
    }
}
