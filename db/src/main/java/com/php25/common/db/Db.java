package com.php25.common.db;

import com.php25.common.db.core.JdbcPair;
import com.php25.common.db.core.execute.BaseSqlExecute;
import com.php25.common.db.core.execute.MysqlSqlExecute;
import com.php25.common.db.core.execute.OracleSqlExecute;
import com.php25.common.db.core.execute.PostgresSqlExecute;
import com.php25.common.db.core.manager.JdbcModelManager;
import com.php25.common.db.core.sql.BaseQuery;
import com.php25.common.db.core.sql.MysqlQuery;
import com.php25.common.db.core.sql.OracleQuery;
import com.php25.common.db.core.sql.PostgresQuery;
import com.php25.common.db.exception.DbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;


/**
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {
    private static final Logger log = LoggerFactory.getLogger(Db.class);

    private JdbcPair jdbcPair;

    private final DbType dbType;

    public DbType getDbType() {
        return dbType;
    }

    public Db(DbType dbType) {
        this.dbType = dbType;
    }

    public JdbcPair getJdbcPair() {
        return jdbcPair;
    }

    public void setJdbcPair(JdbcPair jdbcPair) {
        this.jdbcPair = jdbcPair;
    }

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*:";


    public void scanPackage(String... basePackages) {
        for (String basePackage : basePackages) {
            try {
                String packageSearchPath = CLASSPATH_ALL_URL_PREFIX +
                        resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
                String basePackage0 = basePackage.replace(".", "/");
                for (Resource resource : resources) {
                    String path = resource.getURI().toString();
                    if (path.indexOf(basePackage0) > 0) {
                        String className = path.substring(path.indexOf(basePackage0)).split("\\.")[0].replace("/", ".");
                        Class<?> class0 = ClassUtils.getDefaultClassLoader().loadClass(className);
                        JdbcModelManager.getModelMeta(class0);
                    }
                }
            } catch (Exception e) {
                throw new DbException("Db在扫描包:" + basePackage + "出错", e);
            }
        }
    }

    private ResourcePatternResolver getResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }

    private String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(new StandardEnvironment().resolveRequiredPlaceholders(basePackage));
    }


    public BaseQuery cndJdbc(Class<?> cls) {
        BaseQuery query = null;
        switch (dbType) {
            case MYSQL:
                query = new MysqlQuery(cls);
                break;
            case ORACLE:
                query = new OracleQuery(cls);
                break;
            case POSTGRES:
                query = new PostgresQuery(cls);
                break;
            default:
                query = new MysqlQuery(cls);
                break;
        }
        return query;
    }


    public BaseQuery cndJdbc(Class<?> cls, String alias) {
        BaseQuery query = null;
        switch (dbType) {
            case MYSQL:
                query = new MysqlQuery(cls, alias);
                break;
            case ORACLE:
                query = new OracleQuery(cls, alias);
                break;
            case POSTGRES:
                query = new PostgresQuery(cls, alias);
                break;
            default:
                query = new MysqlQuery(cls, alias);
                break;
        }
        return query;
    }

    public BaseSqlExecute getBaseSqlExecute() {
        BaseSqlExecute baseSqlExecute = null;
        switch (dbType) {
            case MYSQL:
                baseSqlExecute = new MysqlSqlExecute(this.jdbcPair.getJdbcTemplate());
                break;
            case ORACLE:
                baseSqlExecute = new OracleSqlExecute(this.jdbcPair.getJdbcTemplate());
                break;
            case POSTGRES:
                baseSqlExecute = new PostgresSqlExecute(this.jdbcPair.getJdbcTemplate());
                break;
            default:
                baseSqlExecute = new MysqlSqlExecute(this.jdbcPair.getJdbcTemplate());
                break;
        }
        return baseSqlExecute;
    }
}