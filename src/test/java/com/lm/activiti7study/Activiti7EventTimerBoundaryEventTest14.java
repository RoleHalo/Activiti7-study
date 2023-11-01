package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Acriviti7  事件-定时器-边界事件-测试类
 *
 * 使用场景：
 *      一定时间内未审批就发通知
 *      一定时间内未审批就换人审批
 *      。。。。。。
 */
public class Activiti7EventTimerBoundaryEventTest14 {
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
                .addClasspathResource("bpmn/Event-timer-BoundaryEventTask.bpmn20.xml")
                .name("测试流程14-事件-定时器-边界事件")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());

    }

    /**
     * 发起一个流程
     *
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("Event-timer-BoundaryEvent:1:3");//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());
            try {
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //发起后会进入 总经理（LM_unicornis）审批 ，且开启非中断定时器 1分钟未处理会触发通知服务任务事件
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

        //总经理（LM_unicornis）审批后 会进入 财务审批（wangwu） 同时开启中断定时器 1分钟未审批会转接为 财务实习审批（zhaoliu）流程子流程任务 主流程会直接结束
        List<Task> employeeList = taskService.createTaskQuery().taskAssignee("LM_unicornis").list();//模拟查询当前用户的待办流程
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
        List<Task> list = taskService.createTaskQuery().taskAssignee("赵六").list();

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
