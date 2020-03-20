package com.php25.common.jdbcsample.mysql.jmh;

import com.alibaba.druid.pool.DruidDataSource;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.google.common.collect.Lists;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.core.util.DigestUtil;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.jdbcsample.mysql.config.DbConfig;
import com.php25.common.jdbcsample.mysql.model.Company;
import com.php25.common.jdbcsample.mysql.model.Customer;
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
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;
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
public class MysqlJdbcJmhTest {
    private Db db;

    private List<Customer> customers;

    private UidGenerator uidGenerator;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MysqlJdbcJmhTest.class.getSimpleName())
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
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:33306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        druidDataSource.setUsername("root");
        //druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            LoggerFactory.getLogger(DbConfig.class).error("出错啦！", e);
        }
        return druidDataSource;
    }

    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public Db db(JdbcTemplate jdbcTemplate) {
        Db db = new Db(DbType.MYSQL);
        db.setJdbcOperations(jdbcTemplate);
        db.scanPackage("com.php25.common.jdbcsample.mysql.model");
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
        JdbcTemplate jdbcTemplate = jdbcTemplate(dataSource);
        this.db = db(jdbcTemplate);

        db.getJdbcOperations().execute("drop table if exists t_customer");
        db.getJdbcOperations().execute("drop table if exists t_company");
        db.getJdbcOperations().execute("drop table if exists t_department");
        db.getJdbcOperations().execute("drop table if exists t_customer_department");

        db.getJdbcOperations().execute("create table t_customer (id bigint auto_increment primary key,username varchar(20),password varchar(50),age int,create_time datetime,update_time datetime,version bigint,`enable` int,score bigint,company_id bigint)");
        db.getJdbcOperations().execute("create table t_company (id bigint primary key,name varchar(20),create_time datetime,update_time datetime,`enable` int)");
        db.getJdbcOperations().execute("create table t_department (id bigint primary key,name varchar(20))");
        db.getJdbcOperations().execute("create table t_customer_department (customer_id bigint,department_id bigint)");

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
    public void insert() throws Exception {
        Customer customer = new Customer();
        customer.setUsername("jack");
        customer.setPassword(DigestUtil.MD5Str("123456"));
        customer.setAge(10);
        customer.setStartTime(LocalDateTime.now());
        customer.setEnable(1);
        this.db.cndJdbc(Customer.class).insert(customer);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void insert0() throws Exception {
        this.db.getJdbcOperations().update("insert into t_customer(`username`,`password`,`age`,`create_time`,`update_time`,`version`,`enable`,`score`,`company_id`) values (?,?,?,now(),null,?,?,?,?)", new Object[]{"jack", "123456", 12, 1, 1, 0, 0});
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
        this.db.getJdbcOperations().update("update t_customer set username=? where id=?","jack-0",1);
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
        Map<String,Object> map = this.db.getJdbcOperations().queryForMap("select * from t_customer where username = ?",new Object[]{"jack0"});
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }


    @org.openjdk.jmh.annotations.Benchmark
    public void queryById() throws Exception {
        Customer customer = this.db.cndJdbc(Customer.class).whereEq("id", customers.get(0).getId()).single();
        Assertions.assertThat(customer.getId()).isEqualTo(customers.get(0).getId());
    }

    @org.openjdk.jmh.annotations.Benchmark
    public void queryById0() throws Exception {
        Map<String, Object> map = this.db.getJdbcOperations().queryForMap("select * from t_customer where id =?",new Object[]{customers.get(0).getId()});
        Assertions.assertThat(map.get("id")).isEqualTo(customers.get(0).getId());
    }
}
