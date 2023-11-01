package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
/**
 * Acriviti7 入门案例测试类
 * 1. 部署、发起、审批的简单测试
 * 2. 使用固定分配模式
 */
public class Activiti7Test01 {

    /**
     * 获取processsEngine对象的第一种方式
     * 默认的获取方式
     */
    @Test
    public void test1(){
        //通过getDefaultProcessEngine()方法获取流程引擎对象 会加载resources 目录下的 activiti.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }


    /**
     * 获取processsEngine对象的第二种方式
     * 基于java代码获取processEngine对象
     */
    @Test
    public void test2(){
        ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
              //  .setJdbcUrl("jdbc:mysql://localhost:3308/activiti?serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false")
                .setJdbcUrl("jdbc:mysql://localhost:3308/activiti?nullCatalogMeansCurrent=true&serverTimezone=UTC&characterEncoding=utf8&useUnicode=true&useSSL=false")
                .setJdbcUsername("root")
                .setJdbcPassword("mysql1225")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)//activiti在生成表的时候,true 表示数据库中如果已经存在,那么直接使用,如果不存在,则直接创建
                .buildProcessEngine();
        System.out.println("processEngine = "+processEngine);
    }

    /**
     * 流程部署操作
     */
    @Test
    public void test3(){
        //1. 获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2. 完成流程的部署操作，需要通过repositoryService来完成
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3. 完成部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("bpmn/LeaveApply01.bpmn20.xml")
                .name("请假审批测试流程1-入门案例")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 查询当前流程有哪些
     */
    @Test
    public void test4(){
        //1. 获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2. 需要通过repositoryService来完成查询
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //repositoryService.createProcessDefinitionQuery()//查询流程定义信息 查act_re_procdef表
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        System.out.println("流程定义信息如下：");
        for (ProcessDefinition processDefinition:list
             ) {
            System.out.println(processDefinition.getId());
            System.out.println(processDefinition.getName());
        }

        //repositoryService.createDeploymentQuery()//查询流程部署信息
        System.out.println("流程部署信息如下：");
        List<Deployment> list1 = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment:list1
             ) {
            System.out.println(deployment.getId());
            System.out.println(deployment.getName());
        }
    }

    /**
     * 发起一个流程
     * 通过runtimeService获取流程定义的实例
     */
    @Test
    public  void test5() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("MyLeave:1:3");//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }

    /**
     *待办查询
     */
    @Test
    public void test6(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();
        //Task 对象对应的其实就是 act_ru_task 这张表的记录
        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("zhangsan").list();//模拟查询当前用户的待办流程
        if(employeeList!=null && employeeList.size()>0){
            for (Task task : employeeList) {
                System.out.println("task.getId() = " + task.getId());
                System.out.println("task.getName() = " + task.getName());
                System.out.println("task.getAssignee() = " + task.getAssignee());
            }
        }else{
            System.out.println("当前没有待办任务");
        }
    }


    /**
     * 完成待办审批
     */
    @Test
    public void test7(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        //taskService.complete("2505");//模拟1  模拟对当前登录用户（employee）完成当前审批节点 使得 Assignee 从 employee --> zhangsan

        //模拟2 对模拟流程稍作完善
        //List<Task> list = taskService.createTaskQuery().taskAssignee("zhangsan").list();//模拟对当前登录用户（zhangsan）完成当前审批节点使得 Assignee 从 zhangsan --> lisi
        List<Task> list = taskService.createTaskQuery().taskAssignee("lisi").list();//模拟对当前登录用户（lisi）完成当前审批节点  lisi是最后一个 Assignee  所以lisi完成后数据库中该task会被删除，
        if(list!= null && list.size()>0){
            for (Task task : list) {
                //模拟对当前登录用户下的某个task做相关审批处理 完成该流程的本审批节点
                //具体应该根据前端用户的操作数据获取值 进而进行下一步的相关审批
                taskService.complete(task.getId());
            }
        }else {
            System.out.println("当前没有待办任务");
        }
    }







}
