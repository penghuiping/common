package com.php25.common.jdbcsample.oracle.jmh;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.oracle.model.Company;
import com.php25.common.jdbcsample.oracle.model.Customer;
import com.zaxxer.hikari.HikariDataSource;
import org.assertj.core.api.Assertions;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/14 17:01
 * @Description:
 */
@State(Scope.Benchmark)
public class OracleJdbcJmhTest {
    private Db db;

    private List<Customer> customers;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(OracleJdbcJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("30s"))
                .warmupIterations(3)
                .warmupTime(TimeValue.valueOf("10s"))
                .measurementIterations(3)
                .measurementTime(TimeValue.valueOf("30s"))
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }


    public DataSource druidDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        hikariDataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe");
        hikariDataSource.setUsername("system");
        hikariDataSource.setPassword("oracle");
        hikariDataSource.setAutoCommit(true);
        hikariDataSource.setConnectionTimeout(30000);
        hikariDataSource.setIdleTimeout(300000);
        hikariDataSource.setMinimumIdle(1);
        hikariDataSource.setMaxLifetime(1800000);
        hikariDataSource.setMaximumPoolSize(15);
        hikariDataSource.setPoolName("hikariDataSource");
        return hikariDataSource;
    }

    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public Db db(JdbcTemplate jdbcTemplate) {
        Db db = new Db(DbType.ORACLE);
        db.getJdbcPair().setJdbcOperations(jdbcTemplate);
        db.scanPackage("com.php25.common.jdbcsample.oracle.model");
        return db;
    }

    @Setup(Level.Trial)
    public void init() {
        this.druidDataSource();
        this.db = this.db(new JdbcTemplate(druidDataSource()));
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
        this.db.getJdbcPair().getJdbcOperations().execute("drop table t_customer");
        this.db.getJdbcPair().getJdbcOperations().execute("drop table t_company");
        this.db.getJdbcPair().getJdbcOperations().execute("drop table t_department");
        this.db.getJdbcPair().getJdbcOperations().execute("drop table t_customer_department");
        this.db.getJdbcPair().getJdbcOperations().execute("create table t_customer (id number(38,0) primary key,username nvarchar2(20),password nvarchar2(50),age integer ,create_time date,update_time date,version number(38,0),company_id number(38,0),score number(32,0),enable number(1,0))");
        this.db.getJdbcPair().getJdbcOperations().execute("create table t_company (id number(38,0) primary key,name nvarchar2(20),create_time date,update_time date,enable number(1,0))");
        this.db.getJdbcPair().getJdbcOperations().execute("create table t_department (id number(38,0) primary key,name nvarchar2(20))");
        this.db.getJdbcPair().getJdbcOperations().execute("create table t_customer_department (customer_id number(38,0),department_id number(38,0))");
        this.db.getJdbcPair().getJdbcOperations().execute("drop SEQUENCE SEQ_ID");
        this.db.getJdbcPair().getJdbcOperations().execute("CREATE SEQUENCE SEQ_ID");


        CndJdbc cndJpa = this.db.cndJdbc(Customer.class);

        Company company = new Company();
        company.setName("test");
        company.setId(1L);
        company.setCreateTime(new Date());
        company.setEnable(1);

        this.customers = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setId(snowflakeIdWorker.nextId());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setCompanyId(company.getId());
            this.customers.add(customer);
            cndJpa.insert(customer);
        }

    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update() throws Exception {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setUsername("jack-0");
        customer1.setVersion(0L);
        this.db.cndJdbc(Customer.class).update(customer1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update0() throws Exception {
        this.db.getJdbcPair().getJdbcOperations().update("update t_customer set username=? where id=?", "jack-0", 1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete() throws Exception {
        this.db.cndJdbc(Customer.class).whereEq("id", 1).delete();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete0() throws Exception {
        this.db.getJdbcPair().getJdbcOperations().update("delete from t_customer where id=1");
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername() throws Exception {
        Customer customers1 = this.db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assertions.assertThat(customers1.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername0() throws Exception {
        Map<String, Object> map = this.db.getJdbcPair().getJdbcOperations().queryForMap("select * from t_customer where username = ?", new Object[]{"jack0"});
        Assertions.assertThat(map.get("id").toString()).isEqualTo(customers.get(0).getId().toString());
    }
}
