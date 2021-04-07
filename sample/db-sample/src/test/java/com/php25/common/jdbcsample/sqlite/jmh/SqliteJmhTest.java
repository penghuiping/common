package com.php25.common.jdbcsample.sqlite.jmh;

import com.google.common.collect.Lists;
import com.php25.common.core.mess.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.DbType;
import com.php25.common.db.EntitiesScan;
import com.php25.common.db.Queries;
import com.php25.common.db.QueriesExecute;
import com.php25.common.jdbcsample.sqlite.model.Company;
import com.php25.common.jdbcsample.sqlite.model.Customer;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/3/29 21:40
 */
@State(Scope.Benchmark)
public class SqliteJmhTest {

    private final DbType dbType = DbType.SQLITE;
    private List<Customer> customers;
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SqliteJmhTest.class.getSimpleName())
                .mode(Mode.Throughput)
                .timeout(TimeValue.valueOf("60s"))
                .warmupIterations(1)
                .warmupTime(TimeValue.valueOf("60s"))
                .measurementIterations(1)
                .measurementTime(TimeValue.valueOf("60s"))
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    public DataSource druidDataSource() {
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
        sqLiteDataSource.setUrl("jdbc:sqlite:/tmp/test.db");
        return sqLiteDataSource;
    }


    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        return new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    }

    public EntitiesScan db() {
        EntitiesScan db = new EntitiesScan();
        db.scanPackage("com.php25.common.jdbcsample.sqlite.model");
        return db;
    }

    private void initMeta(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("drop table if exists t_customer");
        jdbcTemplate.execute("drop table if exists t_company");
        jdbcTemplate.execute("drop table if exists t_department");
        jdbcTemplate.execute("drop table if exists t_customer_department");
        jdbcTemplate.execute("create table t_customer (id integer primary key autoincrement,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
        jdbcTemplate.execute("create table t_company (id bigint primary key,name varchar(20),create_time datetime,update_time datetime,`enable` int)");
        jdbcTemplate.execute("create table t_department (id bigint primary key,name varchar(20))");
        jdbcTemplate.execute("create table t_customer_department (customer_id bigint,department_id bigint)");
    }

    @Setup(Level.Trial)
    public void init() {
        DataSource dataSource = druidDataSource();
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker();
        jdbcTemplate = jdbcTemplate(dataSource);
        TransactionTemplate transactionTemplate = transactionTemplate(dataSource);
        db();
        initMeta(jdbcTemplate);

        Company company = new Company();
        company.setName("test");
        company.setId(1L);
        company.setCreateTime(new Date());
        company.setEnable(1);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .insert(Queries.of(dbType).from(Company.class).insert(company));

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
            QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                    .insert(Queries.of(dbType).from(Customer.class).insert(customer));
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void insert() throws Exception {
        Customer customer = new Customer();
        customer.setUsername("jack");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setEnable(1);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .insert(Queries.of(dbType).from(Customer.class).insert(customer));
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void insert0() throws Exception {
        this.jdbcTemplate.update("insert into t_customer(`username`,`password`,`age`,`create_time`,`update_time`,`version`,`enable`,`score`,`company_id`) values (?,?,?,?,null,?,?,?,?)", "jack", "123456", LocalDateTime.now(), 12, 1, 1, 0, 0);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update() throws Exception {
        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setUsername("jack-0");
        customer1.setVersion(0L);
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .update(Queries.of(dbType).from(Customer.class).update(customer1));
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void update0() throws Exception {
        this.jdbcTemplate.update("update t_customer set username=? where id=?", "jack-0", 1);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete() throws Exception {
        QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .delete(Queries.of(dbType).from(Customer.class).whereEq("id", 1).delete());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void delete0() throws Exception {
        this.jdbcTemplate.update("delete from t_customer where id=1");
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername() throws Exception {
        Customer customers1 = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .single(Queries.of(dbType).from(Customer.class).whereEq("username", "jack0").single());
        Assertions.assertThat(customers1.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsernameAlias() throws Exception {
        Customer customers1 = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .single(Queries.of(dbType).from(Customer.class, "a").whereEq("a.username", "jack0").single());
        Assertions.assertThat(customers1.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryByUsername0() throws Exception {
        Map<String, Object> map = this.jdbcTemplate.queryForMap("select * from t_customer where username = ?", "jack0");
        Assertions.assertThat(new Long(map.get("id").toString())).isEqualTo(customers.get(0).getId());
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryById() throws Exception {
        Customer customer = QueriesExecute.of(dbType).singleJdbc().with(jdbcTemplate)
                .single(Queries.of(dbType).from(Customer.class).whereEq("id", customers.get(0).getId()).single());
        Assertions.assertThat(customer.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryById0() throws Exception {
        Map<String, Object> map = this.jdbcTemplate.queryForMap("select * from t_customer where id =?", customers.get(0).getId());
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }
}
