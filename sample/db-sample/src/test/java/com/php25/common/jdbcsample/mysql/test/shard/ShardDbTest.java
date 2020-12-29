package com.php25.common.jdbcsample.mysql.test.shard;

import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.jdbcsample.mysql.model.ShardCustomer;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author penghuiping
 * @date 2020/12/24 17:15
 */
public class ShardDbTest {

    @Autowired
    IdGenerator idGeneratorService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    Db db;

    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();

    private void initMeta() throws Exception {
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer_0");
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer_1");
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_department");
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer_department_0");
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer_department_1");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_customer_0 (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_customer_1 (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_department (id bigint primary key,name varchar(20))");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_customer_department_0 (customer_id bigint,department_id bigint)");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_customer_department_1 (customer_id bigint,department_id bigint)");
    }

    @Before
    public void before() throws Exception {
        initMeta();

        for (int i = 0; i < 3; i++) {
            ShardCustomer customer = new ShardCustomer();
            customer.setId(snowflakeIdWorker.nextId());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setUpdateTime(LocalDateTime.now());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            SqlParams sqlParams = db.from(ShardCustomer.class).insert(customer);
            db.getShardSqlExecute().insert(sqlParams);
            Assert.assertNotNull(customer.getId());
        }

        for (int i = 0; i < 3; i++) {
            ShardCustomer customer = new ShardCustomer();
            customer.setId(snowflakeIdWorker.nextId());
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge(i * 20);
            customer.setEnable(0);
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            SqlParams sqlParams1 = db.from(ShardCustomer.class).insert(customer);
            db.getShardSqlExecute().insert(sqlParams1);
            Assert.assertNotNull(customer.getId());
        }
    }
}
