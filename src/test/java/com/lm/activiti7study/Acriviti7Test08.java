package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Acriviti7 身份信息测试类
 * 审批人 ：直接指定assignee属性
 * 候选人 ：candidateUsers属性
 * 候选人组 ：candidateGroups属性 √
 */
public class Acriviti7Test08 {
    /**
     * 流程部署操作
     */
    @Test
    public void test1(){
        //1. 获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2. 完成流程的部署操作，需要通过repositoryService来完成
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3. 完成部署
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("bpmn/LeaveApplyIdentifyInformation02.bpmn20.xml")
                .name("请假审批测试流程8-身份信息-候选人组")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 发起一个流程
     *
     * 发起成功后 数据库表act_ru_task中的候选人assignee是null  因为此时只有候选人组而没有审批人
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("LeaveApply:1:3");//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }

    /**
     *候选人组 待办查询
     */
    @Test
    public void test3(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();

        String group = "人事部";//模拟获取当前用户所在组
        List<Task> employeeList = taskService.createTaskQuery().taskCandidateGroup(group).list();//模拟查询当前用户的待办流程
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
     *候选人组中某候选人 完成 拾取操作
     * 拾取： 候选人组的某候选人 ---> 审批人
     */
    @Test
    public void test4(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();

        String group = "人事部";//模拟获取当前用户所在组
        List<Task> employeeList = taskService.createTaskQuery().taskCandidateGroup(group).list();//模拟查询当前候选人组的待办流程
        if(employeeList!=null && employeeList.size()>0){
            for (Task task : employeeList) {
                System.out.println("task.getId() = " + task.getId());
                System.out.println("task.getName() = " + task.getName());
                System.out.println("task.getAssignee() = " + task.getAssignee());

                //候选人组的王五1 拾取了 这个任务 的审批权限  变成了这个任务的 审批人
               taskService.claim(task.getId(),"王五1");
            }
        }else{
            System.out.println("当前没有待办任务");
        }
    }

    /**
     *候选人 完成 归还操作
     * 归还： 审批人 ---> 候选人
     */
    @Test
    public void test5(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();

        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("王五1").list();//模拟查询当前用户的待办流程
        if(employeeList!=null && employeeList.size()>0){
            for (Task task : employeeList) {
                System.out.println("task.getId() = " + task.getId());
                System.out.println("task.getName() = " + task.getName());
                System.out.println("task.getAssignee() = " + task.getAssignee());

                //张三 归还了 这个任务 的审批权限  变成了这个任务的 候选人
                taskService.unclaim(task.getId());
            }
        }else{
            System.out.println("当前没有待办任务");
        }
    }

    /**
     *拾取任务的人没时间审批 也可以不归还
     * 而是进行任务交接 把这个任务交给另一个人审批
     */
    @Test
    public void test7(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();

        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("王五1").list();//模拟查询当前用户的待办流程
        if(employeeList!=null && employeeList.size()>0){
            for (Task task : employeeList) {
                System.out.println("task.getId() = " + task.getId());
                System.out.println("task.getName() = " + task.getName());
                System.out.println("task.getAssignee() = " + task.getAssignee());

                //张三 归还了 这个任务 的审批权限  变成了这个任务的 候选人
                taskService.setAssignee(task.getId(),"李四1");
            }
        }else{
            System.out.println("当前没有待办任务");
        }
    }
    /**
     * 完成待办审批
     *
     *
     */
    @Test
    public void test6(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskAssignee("王五1").list();
        if(list!= null && list.size()>0){
            for (Task task : list) {
                //模拟对当前登录用户下的某个task做相关审批处理 完成该流程的本审批节点
                //具体应该根据前端用户的操作数据获取值 进而进行下一步的相关审批
                System.out.println("当前处理的流程审批任务的task.getId() = " + task.getId());
                taskService.complete(task.getId());
            }
        }else {
            System.out.println("当前没有待办任务");
        }
    }
}
