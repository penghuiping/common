package com.php25.common.jdbcsample.postgres.jmh;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.sql.BaseQuery;
import com.php25.common.jdbcsample.postgres.model.Company;
import com.php25.common.jdbcsample.postgres.model.Customer;
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
public class PostgresJdbcJmhTest {

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
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName("org.postgresql.Driver");
        hikariDataSource.setJdbcUrl("jdbc:postgresql://127.0.0.1:5432/test?currentSchema=public");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("root");
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
        Db db = new Db(DbType.POSTGRES);
        db.getJdbcPair().setJdbcTemplate(jdbcTemplate);
        db.scanPackage("com.php25.common.jdbcsample.postgres.model");
        return db;
    }

    @Setup(Level.Trial)
    public void init() {
        DataSource dataSource = druidDataSource();
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
        this.db = this.db(jdbcTemplate(dataSource));
        this.db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer");
        this.db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_company");
        this.db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_department");
        this.db.getJdbcPair().getJdbcTemplate().execute("drop table if exists t_customer_department");

        this.db.getJdbcPair().getJdbcTemplate().execute("create table t_customer (id bigint primary key,username varchar(20),password varchar(50),age integer ,create_time timestamp,update_time timestamp,version bigint,company_id bigint,score bigint,enable integer)");
        this.db.getJdbcPair().getJdbcTemplate().execute("create table t_company (id bigint primary key,name varchar(20),create_time timestamp,update_time timestamp,enable integer)");
        this.db.getJdbcPair().getJdbcTemplate().execute("create table t_department (id bigint primary key,name varchar(20))");
        this.db.getJdbcPair().getJdbcTemplate().execute("create table t_customer_department (customer_id bigint,department_id bigint)");

        this.db.getJdbcPair().getJdbcTemplate().execute("drop SEQUENCE if exists SEQ_ID");
        this.db.getJdbcPair().getJdbcTemplate().execute("create SEQUENCE SEQ_ID");

        BaseQuery cndJdbc = this.db.cndJdbc(Customer.class);

        Company company = new Company();
        company.setName("test");
        company.setId(1L);
        company.setCreateTime(new Date());
        company.setEnable(1);
        db.getBaseSqlExecute().insert(db.cndJdbc(Company.class).insert(company));

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
            db.getBaseSqlExecute().insert(cndJdbc.insert(customer));
        }

    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update() throws Exception {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setUsername("jack-0");
        customer1.setVersion(0L);
        db.getBaseSqlExecute().update(this.db.cndJdbc(Customer.class).update(customer1));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update0() throws Exception {
        this.db.getJdbcPair().getJdbcTemplate().update("update t_customer set username=? where id=?", "jack-0", 1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete() throws Exception {
        db.getBaseSqlExecute().delete(this.db.cndJdbc(Customer.class).whereEq("id", 1).delete());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete0() throws Exception {
        this.db.getJdbcPair().getJdbcTemplate().update("delete from t_customer where id=1");
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername() throws Exception {
        Customer customers1 = this.db.getBaseSqlExecute().single(this.db.cndJdbc(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customers1.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername0() throws Exception {
        Map<String, Object> map = this.db.getJdbcPair().getJdbcTemplate().queryForMap("select * from t_customer where username = ?", "jack0");
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryById() throws Exception {
        Customer customer = this.db.getBaseSqlExecute().single(this.db.cndJdbc(Customer.class).whereEq("id", customers.get(0).getId()).single());
        Assertions.assertThat(customer.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryById0() throws Exception {
        Map<String, Object> map = this.db.getJdbcPair().getJdbcTemplate().queryForMap("select * from t_customer where id =?", customers.get(0).getId());
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }
}
