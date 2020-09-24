package com.php25.common.jdbcsample.mysql.test;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.repository.shard.TransactionCallback;
import com.php25.common.db.repository.shard.TwoPhaseCommitTransaction;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Department;
import com.php25.common.jdbcsample.mysql.repository.ShardDepartmentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

/**
 * @author penghuiping
 * @date 2020/9/14 14:08
 */
@SpringBootTest(classes = {CommonAutoConfigure.class})
@ActiveProfiles(value = "many_db")
@RunWith(SpringRunner.class)
public class ShardMysqlJdbcTest {

    private static final Logger log = LoggerFactory.getLogger(ShardMysqlJdbcTest.class);

    @Autowired
    private ShardDepartmentRepository departmentRepository;

    @Autowired
    private TwoPhaseCommitTransaction twoPhaseCommitTransaction;

    @Autowired
    private List<Db> dbList;

    private SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();


    private void initMeta() throws Exception {
        Class<?> cls = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) cls.getConstructors()[0].newInstance();
        Properties properties = new Properties();
        properties.setProperty("user", "root");
        properties.setProperty("password", "root");
        Connection connection = driver.connect("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false", properties);
        Statement statement = connection.createStatement();
        statement.execute("drop table if exists t_department");
        statement.execute("create table t_department (id bigint primary key,name varchar(20))");
        statement.close();
        connection.close();

        Class<?> cls1 = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver1 = (Driver) cls1.getConstructors()[0].newInstance();
        Properties properties1 = new Properties();
        properties1.setProperty("user", "root");
        properties1.setProperty("password", "root");
        Connection connection1 = driver1.connect("jdbc:mysql://localhost:3307/test?useUnicode=true&characterEncoding=utf-8&useSSL=false", properties);
        Statement statement1 = connection1.createStatement();
        statement1.execute("drop table if exists t_department");
        statement1.execute("create table t_department (id bigint primary key,name varchar(20))");
        statement1.close();
        connection1.close();
    }

    @Before
    public void before() throws Exception {
        initMeta();
        Department department = new Department();
        department.setId(snowflakeIdWorker.nextId());
        department.setName("testDepart");
        department.setNew(true);

        Department department1 = new Department();
        department1.setId(snowflakeIdWorker.nextId());
        department1.setName("testDepart1");
        department1.setNew(true);

        log.info("d1:{}", department.getId() % 2);
        log.info("d2:{}", department1.getId() % 2);

        departmentRepository.saveAll(Lists.newArrayList(department, department1));
    }


    @Test
    public void query() throws Exception {
        List<Department> departments = (List<Department>) departmentRepository.findAll();
        log.info("部门信息:{}", JsonUtil.toJson(departments));
    }

    @Test
    public void twoPhaseTransactionTest() throws Exception {
        TransactionCallback<Department> transactionCallback0 = new TransactionCallback<Department>() {
            @Override
            public Department doInTransaction() {
                Department department = new Department();
                department.setId(snowflakeIdWorker.nextId());
                department.setName("testDepart11");
                department.setNew(true);
                getDb().cndJdbc(Department.class).insert(department);
                return department;
            }

            @Override
            public Db getDb() {
                return dbList.get(0);
            }
        };

        TransactionCallback<Department> transactionCallback1 = new TransactionCallback<Department>() {
            @Override
            public Department doInTransaction() {
                Department department = new Department();
                department.setId(snowflakeIdWorker.nextId());
                department.setName("testDepart12");
                department.setNew(true);
                getDb().cndJdbc(Department.class).insert(department);
                return department;
            }

            @Override
            public Db getDb() {
                return dbList.get(1);
            }
        };
        List<Department> departments = twoPhaseCommitTransaction.execute(Lists.newArrayList(transactionCallback0, transactionCallback1));
        log.info("departemnts:{}", JsonUtil.toJson(departments));
    }


}
