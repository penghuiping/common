package com.php25.common;

import com.alibaba.druid.pool.DruidDataSource;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.RepositoryServiceImpl;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * Created by penghuiping on 2018/3/21.
 */
@ComponentScan
public class CommonAutoConfigure {

    @Bean
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useSSL=false");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("root");
        druidDataSource.setMaxActive(15);
        druidDataSource.setTestWhileIdle(false);
        return druidDataSource;
    }

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(DataSource dataSource) {
        StandaloneProcessEngineConfiguration standaloneProcessEngineConfiguration = new StandaloneProcessEngineConfiguration();
        standaloneProcessEngineConfiguration.setDataSource(dataSource);
        standaloneProcessEngineConfiguration.setDatabaseSchemaUpdate("create-drop");
        return standaloneProcessEngineConfiguration;
    }

    @Bean
    public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration) {
        return processEngineConfiguration.buildProcessEngine();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }
}
