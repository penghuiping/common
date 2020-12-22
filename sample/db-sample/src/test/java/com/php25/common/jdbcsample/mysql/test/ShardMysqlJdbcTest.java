package com.php25.common.jdbcsample.mysql.test;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.db.Db;
import com.php25.common.db.repository.shard.TransactionCallback;
import com.php25.common.db.repository.shard.TwoPhaseCommitTransaction;
import com.php25.common.db.specification.Operator;
import com.php25.common.db.specification.SearchParam;
import com.php25.common.db.specification.SearchParamBuilder;
import com.php25.common.jdbcsample.mysql.CommonAutoConfigure;
import com.php25.common.jdbcsample.mysql.model.Department;
import com.php25.common.jdbcsample.mysql.repository.ShardDepartmentRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();


    private void initMeta() throws Exception {
        Db db = dbList.get(0);
        Db db1 = dbList.get(1);
        db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_department");
        db.getJdbcPair().getJdbcTemplate().execute("create table t_department (id bigint primary key,name varchar(20))");
        db1.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_department");
        db1.getJdbcPair().getJdbcTemplate().execute("create table t_department (id bigint primary key,name varchar(20))");
    }

    @Before
    public void before() throws Exception {
        initMeta();
        Department department = new Department(snowflakeIdWorker.nextId(), "testDepart", true);
        Department department1 = new Department(snowflakeIdWorker.nextId(), "testDepart1", true);
        Department department2 = new Department(snowflakeIdWorker.nextId(), "testDepart2", true);
        Department department3 = new Department(snowflakeIdWorker.nextId(), "testDepart23", true);
        Department department4 = new Department(snowflakeIdWorker.nextId(), "testDepart24", true);
        Department department5 = new Department(snowflakeIdWorker.nextId(), "testDepart25", true);
        Department department6 = new Department(snowflakeIdWorker.nextId(), "testDepart26", true);
        Department department7 = new Department(snowflakeIdWorker.nextId(), "testDepart7", true);
        Department department8 = new Department(snowflakeIdWorker.nextId(), "testDepart8", true);
        departmentRepository.saveAll(Lists.newArrayList(department, department1,
                department2, department3, department4, department5, department6, department7, department8));
    }


    @Test
    public void queryPage() throws Exception {
        SearchParamBuilder builder = SearchParamBuilder.builder().append(SearchParam.of("name", Operator.LIKE, "testDepart2%"));
        Page<Department> result1 = departmentRepository.findAll(builder, PageRequest.of(2, 3));
        log.info("部门分页信息:{}", JsonUtil.toJson(result1.getContent()));
        Assertions.assertThat(result1.getContent().get(0).getName()).isEqualTo("testDepart25");
        Assertions.assertThat(result1.getContent().get(1).getName()).isEqualTo("testDepart26");

    }

    @Test
    public void query() throws Exception {
        List<Department> departments = (List<Department>) departmentRepository.findAll();
        log.info("部门信息:{}", JsonUtil.toJson(departments));
        Assertions.assertThat(departments.size()).isEqualTo(2);
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
                getDb().from(Department.class).insert(department);
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
                getDb().from(Department.class).insert(department);
                return department;
            }

            @Override
            public Db getDb() {
                return dbList.get(1);
            }
        };
        List<Department> departments = twoPhaseCommitTransaction.execute(transactionCallback0, transactionCallback1);
        log.info("部门信息:{}", JsonUtil.toJson(departments));

        departments = (List<Department>) departmentRepository.findAll();
        log.info("部门信息:{}", JsonUtil.toJson(departments));
        Assertions.assertThat(departments.size()).isEqualTo(4);
    }


    @Test
    public void twoPhaseTransactionTest1() throws Exception {
        TransactionCallback<Department> transactionCallback0 = new TransactionCallback<Department>() {
            @Override
            public Department doInTransaction() {
                Department department = new Department();
                department.setId(snowflakeIdWorker.nextId());
                department.setName("testDepart11");
                department.setNew(true);
                int i = 1 / 0;
                getDb().from(Department.class).insert(department);
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
                getDb().from(Department.class).insert(department);
                return department;
            }

            @Override
            public Db getDb() {
                return dbList.get(1);
            }
        };
        List<Department> departments = twoPhaseCommitTransaction.execute(Lists.newArrayList(transactionCallback0, transactionCallback1));
        log.info("部门信息:{}", JsonUtil.toJson(departments));

        departments = (List<Department>) departmentRepository.findAll();
        log.info("部门信息:{}", JsonUtil.toJson(departments));
        Assertions.assertThat(departments.size()).isEqualTo(2);
    }


}
