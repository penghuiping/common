package com.php25.common.jdbcsample.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.core.service.IdGeneratorService;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by penghuiping on 2018/5/1.
 */
@Configuration
public class DruidConfig {

    @Autowired
    private IdGeneratorService idGeneratorService;


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("org.h2.Driver");
        druidDataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;");
        druidDataSource.setUsername("");
        druidDataSource.setPassword("");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        try {
            druidDataSource.setFilters("stat, wall");
        } catch (SQLException e) {
            LoggerFactory.getLogger(DruidConfig.class).error("出错啦！", e);
        }
        return druidDataSource;
    }

    @Bean
    MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage("com.php25.common.jdbcsample.mapper");
        return mapperScannerConfigurer;
    }


    @Bean
    MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean(@Autowired DataSource dataSource, @Autowired OptimisticLockerInterceptor optimisticLockerInterceptor) {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        mybatisSqlSessionFactoryBean.setPlugins(new Interceptor[]{optimisticLockerInterceptor});
        return mybatisSqlSessionFactoryBean;
    }

    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

}
