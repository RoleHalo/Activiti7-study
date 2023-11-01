package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
/**
 * Acriviti7 任务分配测试类
 * 使用UEl表达式
 *      1. 值表达式
 *      2. 方法表达式
 * 监听器分配 √
 */
public class Acriviti7Test04 {
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
                .addClasspathResource("bpmn/LeaveApplyListener.bpmn20.xml")
                .name("请假审批测试流程4-任务分配-监听器形式")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    /**
     * 通过runtimeService获取流程定义的实例
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("LeaveApply:2:2503");//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }

    /**
     * 完成待办审批
     */
    @Test
    public void test4(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        //taskService.complete("5005");//也可以进行模拟。 act_ru_task表。 模拟对当前登录用户（wangwu）完成当前审批节点 使得 Assignee 从 wangwu --> 暂时未定义
        List<Task> list = taskService.createTaskQuery().taskAssignee("wangwu").list();//模拟对当前登录用户（wangwu）完成当前审批节点 使得 Assignee 从 wangwu --> 暂时未定义
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
