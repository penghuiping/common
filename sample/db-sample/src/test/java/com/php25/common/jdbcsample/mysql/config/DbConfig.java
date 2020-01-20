package com.php25.common.jdbcsample.mysql.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.exception.UidGenerateException;
import com.php25.common.core.service.SnowflakeIdWorker;
import com.php25.common.db.Db;
import com.php25.common.db.DbType;
import com.php25.common.db.manager.JdbcModelManager;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Configuration
public class DbConfig {
    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            LoggerFactory.getLogger(DbConfig.class).error("出错啦！", e);
        }
        return druidDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    Db db(JdbcTemplate jdbcTemplate) {
        Db db =  new Db(DbType.MYSQL);
        db.setJdbcOperations(jdbcTemplate);
        return db;
    }

    @Bean
    SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }

    @Bean
    UidGenerator uidGenerator(SnowflakeIdWorker snowflakeIdWorker) {
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

    @PostConstruct
    public void init() {
        String basePackage = "com.php25.common.jdbcsample.mysql.model";
        try {
            String basePackage1 = basePackage.replace(".","/");
            Enumeration<URL> urlEnumeration = ClassUtils.getDefaultClassLoader().getResources(basePackage1);
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                if("file".equals(url.getProtocol())) {
                    String fileBasePath =  url.getPath();
                    Stream<Path> pathStream =  Files.list(Paths.get(fileBasePath));
                    Set<String> result = pathStream.filter(path -> path.toString().endsWith(".class"))
                            .map(path -> {
                                String tmp = path.getFileName().toString();
                                return tmp.substring(0,tmp.length()-6);
                            }).collect(Collectors.toSet());

                    Iterator<String> iterator = result.iterator();
                    while (iterator.hasNext()) {
                        String className = iterator.next();
                        try {
                            Class class0 = ClassUtils.getDefaultClassLoader().loadClass( basePackage+"."+className);
                            JdbcModelManager.getModelMeta(class0);
                            System.out.println("初始化"+basePackage+className);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
