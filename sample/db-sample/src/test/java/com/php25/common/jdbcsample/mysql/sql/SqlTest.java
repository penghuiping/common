package com.php25.common.jdbcsample.mysql.sql;

import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.core.sql.MysqlQuery;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author penghuiping
 * @date 2020/12/2 17:46
 */
public class SqlTest {
    private static final Logger log = LoggerFactory.getLogger(SqlTest.class);


    @Test
    public void insertSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class);
        Customer customer = new Customer();
        customer.setAge(12);
        customer.setEnable(1);
        customer.setPassword("123456");
        customer.setUsername("jack");
        SqlParams sqlParams = mysqlQuery.insert(customer);
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }

    public void insertBatchSql() {

    }

    @Test
    public void updateSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class);
        Customer customer = new Customer();
        customer.setAge(12);
        customer.setEnable(1);
        customer.setPassword("123456");
        customer.setUsername("jack");
        customer.setId(1L);
        SqlParams sqlParams = mysqlQuery.update(customer);
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }

    public void updateBatchSql() {

    }


    @Test
    public void deleteSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class);
        SqlParams sqlParams = mysqlQuery.delete();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }

    @Test
    public void singleSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class, "a");
        SqlParams sqlParams = mysqlQuery.whereEq("a.id", 1).single();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }

    @Test
    public void selectSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class, "a");
        SqlParams sqlParams = mysqlQuery.select();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));

        sqlParams = mysqlQuery.whereEq("a.username", "jack").select();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));

        sqlParams = mysqlQuery.join(Company.class, "b").on("a.companyId", "b.id").whereEq("a.username", "jack").select();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }

    @Test
    public void countSql() {
        MysqlQuery mysqlQuery = new MysqlQuery(Customer.class);
        SqlParams sqlParams = mysqlQuery.whereEq("username", "jack").count();
        log.info("sqlParams info:{}", JsonUtil.toPrettyJson(sqlParams));
    }


}
