package com.php25.common.jdbcsample.mysql.test;

import com.baidu.fsg.uid.UidGenerator;
import com.google.common.collect.Lists;
import com.php25.common.core.util.JsonUtil;
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
    private UidGenerator uidGenerator;

    @Autowired
    private ShardDepartmentRepository departmentRepository;


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
        department.setId(uidGenerator.getUID());
        department.setName("testDepart");
        department.setNew(true);

        Department department1 = new Department();
        department1.setId(uidGenerator.getUID());
        department1.setName("testDepart1");
        department1.setNew(true);

        log.info("d1:{}",department.getId()%2);
        log.info("d2:{}",department1.getId()%2);

        departmentRepository.saveAll(Lists.newArrayList(department, department1));
    }


    @Test
    public void query() throws Exception {
        List<Department> departments = (List<Department>)departmentRepository.findAll();
        log.info("部门信息:{}", JsonUtil.toJson(departments));
    }


}