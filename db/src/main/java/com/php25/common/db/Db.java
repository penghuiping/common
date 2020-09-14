package com.php25.common.db;

import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.db.cnd.JdbcPair;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

/**
 *
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {
    private static final Logger log = LoggerFactory.getLogger(Db.class);

    private JdbcPair jdbcPair;

    private DbType dbType;

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


    public void scanPackage(String ...basePackages) {
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

    /**
     * 获取一个关系型数据库 新条件
     *
     * @return
     */
    public CndJdbc cndJdbc(Class cls) {
        return CndJdbc.of(cls, null, dbType, this.jdbcPair.getJdbcOperations());
    }

    /**
     * 获取一个关系型数据库 新条件
     *
     * @return
     */
    public CndJdbc cndJdbc(Class cls, String alias) {
        return CndJdbc.of(cls, alias, dbType, this.jdbcPair.getJdbcOperations());
    }


}