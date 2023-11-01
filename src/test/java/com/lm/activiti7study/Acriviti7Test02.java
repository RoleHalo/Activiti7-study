package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Acriviti7 任务分配测试类
 * 使用UEl表达式
 *  1. 值表达式 √
 *  2. 方法表达式
 */
public class Acriviti7Test02 {
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
                .addClasspathResource("bpmn/LeaveApply02.bpmn20.xml")
                .name("请假审批测试流程2-任务分配-值表达式")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    /**
     * 使用值表达式（如：${assignee1}）后发起一个流程需要传入流程变量
     * 通过runtimeService获取流程定义的实例
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //对流程变量赋值 startProcessInstanceById有重载 可通过map参数传入流程变量
        Map<String,Object> map = new HashMap<>();
        map.put("assignee1","王五");
        map.put("assignee2","张三");
        map.put("assignee3","李四");
        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("MyLeave:1:3",map);//模拟测试发起一个流程

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
        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("王五").list();//模拟查询当前用户的待办流程
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
        //taskService.complete("5008");//模拟1。 act_ru_task表。 模拟对当前登录用户（王五）完成当前审批节点 使得 Assignee 从 王五 --> 张三

        //模拟2 对模拟流程稍作完善
        //List<Task> list = taskService.createTaskQuery().taskAssignee("张三").list();//模拟对当前登录用户（张三）完成当前审批节点使得 Assignee 从 张三 --> 李四
        List<Task> list = taskService.createTaskQuery().taskAssignee("李四").list();//模拟对当前登录用户（李四）完成当前审批节点  李四是最后一个 Assignee  所以李四完成后数据库中该task会被删除，
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
