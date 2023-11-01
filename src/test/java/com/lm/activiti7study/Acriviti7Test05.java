package com.lm.activiti7study;

import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Acriviti7 流程变量-全局变量测试类
 * 运行时变量:
 *      全局变量 √
 *      局部变量
 * 历史变量
 */
public class Acriviti7Test05 {
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
                .addClasspathResource("bpmn/LeaveApplyProcessVariable01.bpmn20.xml")
                .name("请假审批测试流程5-流程变量-运行时全局流程变量")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    /**
     * 设置全局流程变量
     */
    @Test
    public  void test2() {
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String,Object> map = new HashMap<>();
        map.put("assignee1","assignee1张");
        map.put("assignee2","assignee2李");
        map.put("assignee3","assignee3王");
        map.put("as1","赵");
        map.put("as2","周");
        //传参 且此时会触发监听器中的修改办法
        //可以在act_ru_variable表中看到
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("LeaveApply:3:7503",map);//模拟测试发起一个流程

        System.out.println("processInstance.getId() = " + processInstance.getId());
        System.out.println("processInstance.getDeploymentId() = " + processInstance.getDeploymentId());
        System.out.println("processInstance.getDescription() = " + processInstance.getDescription());

    }
    /**
     *
     * 查询当前流程变量信息
     */
    @Test
    public void test4(){
        //获取processEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //查 某个流程实例 的变量信息 而不是某个流程定义下所有实例的全部流程变量
        //传参 执行时ID   ==》 查act_ru_task中的 Execution_ID
        Map<String, VariableInstance> variableInstances = runtimeService.getVariableInstances("10007");
        System.out.println("----->获取后输出当前流程实例变量");
        Set<String> keySet = variableInstances.keySet();
        for (String key : keySet) {
            System.out.println(key+"="+variableInstances.get(key));
        }
    }


    /**
     * 完成待办审批
     *  全局变量会随着流程完成而消失
     */
    @Test
    public void test5(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService = processEngine.getTaskService();
        List<Task> list = taskService.createTaskQuery().taskAssignee("assignee3王-lm123").list();
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
