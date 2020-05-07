package com.php25.common.db;

import com.php25.common.db.cnd.CndJdbc;
import com.php25.common.db.exception.DbException;
import com.php25.common.db.manager.JdbcModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.ClassUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author penghuiping
 * @date 2018-08-23
 */
public class Db {
    private static final Logger log = LoggerFactory.getLogger(Db.class);
    private JdbcOperations jdbcOperations;

    private DbType dbType;

    public DbType getDbType() {
        return dbType;
    }

    public Db(DbType dbType) {
        this.dbType = dbType;
    }

    public void setJdbcOperations(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public JdbcOperations getJdbcOperations() {
        return jdbcOperations;
    }

    public void scanPackage(String basePackage) {
        try {
            String basePackage1 = basePackage.replace(".", "/");
            Enumeration<URL> urlEnumeration = ClassUtils.getDefaultClassLoader().getResources(basePackage1);
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                if ("file".equals(url.getProtocol())) {
                    String fileBasePath = url.getPath();
                    Stream<Path> pathStream = Files.list(Paths.get(fileBasePath));
                    Set<String> result = pathStream.filter(path -> path.toString().endsWith(".class"))
                            .map(path -> {
                                String tmp = path.getFileName().toString();
                                return tmp.substring(0, tmp.length() - 6);
                            }).collect(Collectors.toSet());

                    Iterator<String> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        String className = iterator.next();
                        Class class0 = ClassUtils.getDefaultClassLoader().loadClass(basePackage + "." + className);
                        JdbcModelManager.getModelMeta(class0);

                    }
                }
            }
        } catch (Exception e) {
            throw new DbException("Db在扫描包:" + basePackage + "出错", e);
        }
    }

    /**
     * 获取一个关系型数据库 新条件
     *
     * @return
     */
    public CndJdbc cndJdbc(Class cls) {
        return CndJdbc.of(cls, dbType, this.jdbcOperations);
    }


}