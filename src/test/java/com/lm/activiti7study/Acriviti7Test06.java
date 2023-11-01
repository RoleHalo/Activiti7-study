package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Acriviti7 流程变量测试类
 * 运行时变量:
 *      全局变量
 *      局部变量 √
 * 历史变量 √
 *
 * 局部变量，通过某网关分支后，局部变量会变得不同
 */
public class Acriviti7Test06 {
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
                .addClasspathResource("bpmn/LeaveApplyProcessVariable02.bpmn20.xml")
                .name("请假审批测试流程6-流程变量-局部流程变量")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }

    /**
     * runtimeService 设置全局流程变量
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //此处为设置全局变量
        Map<String,Object> map = new HashMap<>();
        map.put("assignee1","assignee1张");
        map.put("assignee2","assignee2李");
        map.put("assignee3","assignee3王");

        //通过runtimeService获取流程定义的一个流程实例对象    流程定义：查act_re_procdef表
        //使用值表达式需要传入全局变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("LeaveApply:4:20003",map);//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }


    /**
     * runtimeService 设置局部变量
     */
    @Test
    public void test3(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //传参  运行时ID及局部变量键值对形式
        runtimeService.setVariableLocal("22505","leaveId","123");
        runtimeService.setVariableLocal("22505","name","666");
    }

    /**
     * TaskService 设置局部变量
     */
    @Test
    public void test5(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.setVariableLocal("22508","taskServiceTest","6666666");
    }

    /**
     * 读取局部变量
     */
    @Test
    public void test4(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        //传参  运行时ID  查act_ru_task的Execution_ID
        Map<String, Object> variablesLocal = runtimeService.getVariablesLocal("22505");
        System.out.println("------->runtimeService获取当前流程实例的局部变量");
        Set<String> keySet = variablesLocal.keySet();
        for (String key : keySet) {
            System.out.println(key+"="+variablesLocal.get(key));
        }

        //传参  任务ID  查act_ru_task的ID
        Map<String, Object> variablesLocal1 = taskService.getVariablesLocal("22508");
        System.out.println("------->taskService获取当前流程实例的局部变量");
        Set<String> keySet1 = variablesLocal1.keySet();
        for (String key : keySet1) {
            System.out.println(key+"="+variablesLocal1.get(key));
        }
    }

    /**
     * 读取历史变量
     */
    @Test
    public void test7(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        HistoryService historyService = processEngine.getHistoryService();

        //historyService.createHistoricVariableInstanceQuery()
        // 所有工作流的历史数据都要通过historyService实现查询
        //存在act_hi_varinst表中 不会被删除 会”永久“保存
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().list();
        System.out.println("------->historyService获取当前流程实例的历史流程变量");
        for (HistoricVariableInstance historicVariableInstance : list) {
            System.out.println("historicVariableInstance = " + historicVariableInstance);
        }
    }


    /**
     * 完成待办审批
     *
     * TaskService 设置局部变量只会在当前节点审批完成前存在
     * 该节点审批完成后会消失
     *
     */
    @Test
    public void test6(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskAssignee("assignee1张").list();
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
