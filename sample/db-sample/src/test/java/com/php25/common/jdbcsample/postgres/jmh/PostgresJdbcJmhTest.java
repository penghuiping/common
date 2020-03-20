package com.php25.common.jdbcsample.postgres.jmh;

import com.alibaba.druid.pool.DruidDataSource;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.google.common.collect.Lists;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.postgres.model.Company;
import com.php25.common.jdbcsample.postgres.model.Customer;
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
public class PostgresJdbcJmhTest {
    private UidGenerator uidGenerator;

    private Db db;

    private List<Customer> customers;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PostgresJdbcJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("30s"))
                .warmupIterations(3)
                .warmupTime(TimeValue.valueOf("10s"))
                .measurementIterations(5)
                .measurementTime(TimeValue.valueOf("30s"))
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.postgresql.Driver");
        druidDataSource.setUrl("jdbc:postgresql://127.0.0.1:35432/test?currentSchema=public");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        return druidDataSource;
    }

    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public Db db(JdbcTemplate jdbcTemplate) {
        Db db = new Db(DbType.POSTGRES);
        db.setJdbcOperations(jdbcTemplate);
        db.scanPackage("com.php25.common.jdbcsample.postgres.model");
        return db;
    }

    public UidGenerator uidGenerator(SnowflakeIdWorker snowflakeIdWorker) {
        return new UidGenerator() {
            @Override
            public long getUID() throws UidGenerateException {
                return snowflakeIdWorker.nextId();
            }

            @Override
            public String parseUID(long uid) {
                return uid + "";
            }
        };
    }

    @Setup(Level.Trial)
    public void init() {
        DataSource dataSource = druidDataSource();
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
        this.uidGenerator = uidGenerator(snowflakeIdWorker);
        this.db = this.db(jdbcTemplate(dataSource));
        this.db.getJdbcOperations().execute("drop table if exists t_customer");
        this.db.getJdbcOperations().execute("drop table if exists t_company");
        this.db.getJdbcOperations().execute("drop table if exists t_department");
        this.db.getJdbcOperations().execute("drop table if exists t_customer_department");

        this.db.getJdbcOperations().execute("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age integer ,create_time timestamp,update_time timestamp,version bigint,company_id bigint,score bigint,enable integer)");
        this.db.getJdbcOperations().execute("create table t_company (id bigint primary key,name varchar(20),create_time timestamp,update_time timestamp,enable integer)");
        this.db.getJdbcOperations().execute("create table t_department (id bigint primary key,name varchar(20))");
        this.db.getJdbcOperations().execute("create table t_customer_department (customer_id bigint,department_id bigint)");

        this.db.getJdbcOperations().execute("drop SEQUENCE if exists SEQ_ID");
        this.db.getJdbcOperations().execute("create SEQUENCE SEQ_ID");

        CndJdbc cndJdbc = this.db.cndJdbc(Customer.class);

        Company company = new Company();
        company.setName("test");
        company.setId(1L);
        company.setCreateTime(new Date());
        company.setEnable(1);
        db.cndJdbc(Company.class).insert(company);

        this.customers = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            Customer customer = new Customer();
            customer.setId(uidGenerator.getUID());
            customer.setUsername("jack" + i);
            customer.setPassword(DigestUtil.MD5Str("123456"));
            customer.setAge(i * 10);
            customer.setStartTime(LocalDateTime.now());
            customer.setEnable(1);
            customer.setCompanyId(company.getId());
            this.customers.add(customer);
            cndJdbc.insert(customer);
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
        this.db.getJdbcOperations().update("update t_customer set username=? where id=?", "jack-0", 1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete() throws Exception {
        this.db.cndJdbc(Customer.class).whereEq("id", 1).delete();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete0() throws Exception {
        this.db.getJdbcOperations().update("delete from t_customer where id=1");
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername() throws Exception {
        Customer customers1 = this.db.cndJdbc(Customer.class).whereEq("username", "jack0").single();
        Assertions.assertThat(customers1.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername0() throws Exception {
        Map<String, Object> map = this.db.getJdbcOperations().queryForMap("select * from t_customer where username = ?", new Object[]{"jack0"});
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryById() throws Exception {
        Customer customer = this.db.cndJdbc(Customer.class).whereEq("id", customers.get(0).getId()).single();
        Assertions.assertThat(customer.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryById0() throws Exception {
        Map<String, Object> map = this.db.getJdbcOperations().queryForMap("select * from t_customer where id =?", new Object[]{customers.get(0).getId()});
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }
}
