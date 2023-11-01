package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Acriviti7  事件-消息事件-启动事件 测试类
 *
 * 流程图绘制时候需要进行 · 消息事件的绑定 · 需要提前在流程图中定义消息以及对指定消息节点绑定消息
 */
public class Activiti7EventMessageBeginningEventTest15 {
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
                .addClasspathResource("bpmn/Event-message-beginningTask.bpmn20.xml")
                .name("测试流程15-事件-消息事件-启动事件")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * 发起一个流程
     *
     * 通过传入启动事件定义的消息名字（messagename）启动一个流程实例
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();


        //传入的应该是act_ru_event_subscr表中的event_name  即流程图中定义的消息名称 而不是消息ID
        //消息的name和id需要一致 否则会报错说找不到  具体原因？？？
        ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("firMsg");

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

        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("张三").list();//模拟查询当前用户的待办流程
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
        List<Task> list = taskService.createTaskQuery().taskAssignee("张三").list();

        if(list!= null && list.size()>0){
            for (Task task : list) {
                //模拟对当前登录用户下的某个task做相关审批处理 完成该流程的本审批节点
                //具体应该根据前端用户的操作数据获取值 进而进行下一步的相关审批
                System.out.println("当前处理的流程审批任务的task.getId() = " + task.getId());
                taskService.complete(task.getId());////模拟对当前登录用户完成当前审批节点 且请假天数大于三天时 按流程下一个审批人会选择 王五而不是李四
            }

        }else {
            System.out.println("当前没有待办任务");
        }
    }
















}
