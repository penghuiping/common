package com.php25.common.jdbcsample.postgres.test.shard;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.IdGenerator;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.db.core.shard.ShardRule;
import com.php25.common.db.core.shard.ShardRuleHashBased;
import com.php25.common.db.core.shard.ShardTableInfo;
import com.php25.common.db.core.sql.SqlParams;
import com.php25.common.db.repository.shard.TwoPhaseCommitTransaction;
import com.php25.common.jdbcsample.mysql.model.ShardCustomer;
import com.php25.common.jdbcsample.mysql.model.ShardDepartment;
import com.php25.common.jdbcsample.mysql.model.ShardDepartmentRef;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    TwoPhaseCommitTransaction twoPhaseCommitTransaction;

    @Autowired
    TransactionTemplate transactionTemplate;

    List<ShardCustomer> customers = Lists.newArrayList();

    private void initMeta() throws Exception {
        jdbcTemplate.execute("drop table if exists t_customer_0");
        jdbcTemplate.execute("drop table if exists t_customer_1");
        jdbcTemplate.execute("drop table if exists t_department");
        jdbcTemplate.execute("drop table if exists t_customer_department_0");
        jdbcTemplate.execute("drop table if exists t_customer_department_1");
        jdbcTemplate.execute("create table t_customer_0 (id bigint primary key,username varchar(20),password varchar(50),age integer ,create_time timestamp,update_time timestamp,version bigint,company_id bigint,score bigint,enable integer)");
        jdbcTemplate.execute("create table t_customer_1 (id bigint primary key,username varchar(20),password varchar(50),age integer ,create_time timestamp,update_time timestamp,version bigint,company_id bigint,score bigint,enable integer)");
        jdbcTemplate.execute("create table t_department (id bigint primary key,name varchar(20))");
        jdbcTemplate.execute("create table t_customer_department_0 (customer_id bigint,department_id bigint)");
        jdbcTemplate.execute("create table t_customer_department_1 (customer_id bigint,department_id bigint)");
    }

    @Before
    public void before() throws Exception {
        initMeta();
        //ShardDepartment
        ShardDepartment shardDepartment = new ShardDepartment();
        shardDepartment.setId(1L);
        shardDepartment.setName("it");
        SqlParams sqlParams0 = Queries.postgres().from(ShardDepartment.class).insert(shardDepartment);
        QueriesExecute.postgres().singleJdbc().with(jdbcTemplate).insert(sqlParams0);

        //shardCustomers
        long id = 1;
        List<JdbcTemplate> jdbcTemplates = Lists.newArrayList(jdbcTemplate, jdbcTemplate);
        List<String> physicalTableNames = Lists.newArrayList("t_customer_0", "t_customer_1");
        ShardRule shardRule = new ShardRuleHashBased();
        List<String> physicalTableNames1 = Lists.newArrayList("t_customer_department_0", "t_customer_department_1");

        for (int i = 0; i < 3; i++) {
            ShardCustomer customer = new ShardCustomer();
            customer.setId(id);
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setUpdateTime(LocalDateTime.now());
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams = Queries.postgres().from(ShardCustomer.class).insert(customer);
            QueriesExecute.postgres().shardJdbc()
                    .with(jdbcTemplates)
                    .with(ShardTableInfo.of(ShardCustomer.class, physicalTableNames).shardRule(shardRule, id))
                    .insert(sqlParams);
            Assert.assertNotNull(customer.getId());

            //ShardDepartmentRef
            ShardDepartmentRef shardDepartmentRef = new ShardDepartmentRef();
            shardDepartmentRef.setCustomerId(id);
            shardDepartmentRef.setDepartmentId(shardDepartment.getId());
            SqlParams sqlParams1 = Queries.postgres().from(ShardDepartmentRef.class).insert(shardDepartmentRef);
            QueriesExecute.postgres().shardJdbc()
                    .with(jdbcTemplates)
                    .with(ShardTableInfo.of(ShardDepartmentRef.class, physicalTableNames1).shardRule(shardRule, id))
                    .insert(sqlParams1);
            id++;
        }

        for (int i = 0; i < 3; i++) {
            ShardCustomer customer = new ShardCustomer();
            customer.setId(id);
            customer.setUsername("mary" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setStartTime(LocalDateTime.now());
            customer.setAge(i * 10);
            customer.setEnable(0);
            customer.setScore(BigDecimal.valueOf(1000L));
            customer.setNew(true);
            customers.add(customer);
            SqlParams sqlParams1 = Queries.postgres().from(ShardCustomer.class).insert(customer);
            QueriesExecute.postgres().shardJdbc()
                    .with(jdbcTemplates)
                    .with(ShardTableInfo.of(ShardCustomer.class, physicalTableNames).shardRule(shardRule, id))
                    .insert(sqlParams1);
            Assert.assertNotNull(customer.getId());

            //ShardDepartmentRef
            ShardDepartmentRef shardDepartmentRef = new ShardDepartmentRef();
            shardDepartmentRef.setCustomerId(id);
            shardDepartmentRef.setDepartmentId(shardDepartment.getId());
            SqlParams sqlParams2 = Queries.postgres().from(ShardDepartmentRef.class).insert(shardDepartmentRef);
            QueriesExecute.postgres().shardJdbc()
                    .with(jdbcTemplates)
                    .with(ShardTableInfo.of(ShardDepartmentRef.class, physicalTableNames1).shardRule(shardRule, id))
                    .insert(sqlParams2);

            id++;
        }

        System.out.println();
        System.out.println("==========================start=============================");
    }

    @After
    public void end() {
        System.out.println("==========================end=============================");
        System.out.println();
    }
}

