package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acriviti7 任务分配测试类
 * 使用UEl表达式
 *  1. 值表达式
 *  2. 方法表达式 √
 */
public class Acriviti7Test03 {
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
                .addClasspathResource("bpmn/LeaveApply03.bpmn20.xml")
                .name("请假审批测试流程3-任务分配-方法表达式")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    /**
     * 使用bean通过方法表达式（如：${myBean.getAssignee()}）后发起一个流程无需传入额外的流程变量
     * 容器会自动注入
     * 通过runtimeService获取流程定义的实例
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("MyLeave:2:15003");//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }

    /**
     *待办查询
     */
    @Test
    public void test3(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //查询待办
        // 执行中的任务处理是通过 TaskService来实现
        TaskService taskService = processEngine.getTaskService();
        //Task 对象对应的其实就是 act_ru_task 这张表的记录
        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("赵六").list();//模拟查询当前用户的待办流程
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
    public void test4(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        //taskService.complete("17505");//也可以进行模拟。 act_ru_task表。 模拟对当前登录用户（赵六）完成当前审批节点 使得 Assignee 从 赵六 --> 暂时未定义
        List<Task> list = taskService.createTaskQuery().taskAssignee("赵六").list();//模拟对当前登录用户（赵六）完成当前审批节点 使得 Assignee 从 赵六 --> 暂时未定义
        if(list!= null && list.size()>0){
            for (Task task : list) {
                //模拟对当前登录用户下的某个task做相关审批处理 完成该流程的本审批节点
                //具体应该根据前端用户的操作数据获取值 进而进行下一步的相关审批
                System.out.println("当前处理的流程审批任务的task.getId() = " + task.getId());//结果是 17505 √
                taskService.complete(task.getId());
            }
        }else {
            System.out.println("当前没有待办任务");
        }
    }
}
