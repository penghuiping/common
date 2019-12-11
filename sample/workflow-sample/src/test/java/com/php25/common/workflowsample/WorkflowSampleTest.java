package com.php25.common.workflowsample;

import com.google.common.collect.Lists;
import com.php25.common.CommonAutoConfigure;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by penghuiping on 2018/5/1.
 */
@SpringBootTest(classes ={CommonAutoConfigure.class})
@RunWith(SpringRunner.class)
public class WorkflowSampleTest {

    private static final Logger log = LoggerFactory.getLogger(WorkflowSampleTest.class);

    @ClassRule
    public static GenericContainer mysql = new GenericContainer<>("mysql:5.7").withExposedPorts(3306);

    static {
        mysql.setPortBindings(Lists.newArrayList("3306:3306"));
        mysql.withEnv("MYSQL_USER", "root");
        mysql.withEnv("MYSQL_ROOT_PASSWORD", "root");
        mysql.withEnv("MYSQL_DATABASE", "test");
    }

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired

    @Before
    public void setUp() {
        repositoryService.createDeployment().addClasspathResource("processes/holiday-request.bpmn20.xml").deploy();
    }

    @Test
    public void test1() throws Exception {
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().active().list();
        for (ProcessDefinition processDefinition : processDefinitions) {
            log.info(processDefinition.getName());
        }

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("employee", "jack");
        variables.put("nrOfHolidays", 2);
        variables.put("description", "家里有事情");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        //查询经理级别的组的任务
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Map<String, Object> vars = taskService.getVariables(task.getId());
            String employeeName = vars.get("employee").toString();
            String holidayNumbers = vars.get("nrOfHolidays").toString();
            String description = vars.get("description").toString();

            //经理查看请假内容
            String content = String.format("姓名为:%s,想要请假%s天,原因是:%s", employeeName, holidayNumbers, description);
            log.info("请假条的内容为:{}", content);

            //经理同意请假申请
            variables = new HashMap<String, Object>();
            variables.put("approved", true);
            taskService.complete(task.getId(), variables);
        }

        //jack查看请假申请结果
        List<Task> tasks1 = taskService.createTaskQuery().taskAssignee("jack").list();
        for (int i = 0; i < tasks1.size(); i++) {
            Task task = tasks1.get(i);
            log.info(task.getName());
            taskService.complete(task.getId());
        }



    }


}
