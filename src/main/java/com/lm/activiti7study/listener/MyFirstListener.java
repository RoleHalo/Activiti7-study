package com.lm.activiti7study.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Service;

/**
 * 监听器分为三种：JavaDelegate TaskListener ExecutionListener
 *
 * 用户任务（UserTask）的监听器为TaskListener
 *
 * Java服务任务（Service Task）的监听器为JavaDelegate
 *
 * 其他的服务的监听器为ExecutionListener
 *
 * TaskListener中参数（DelegateTask）是有关于userTask的
 *
 * JavaDelegate和ExecutionListener参数（DelegateExecution）是有关于流程的
 */

//此类定义的是用户进程监听器
public class MyFirstListener implements TaskListener {

    /**
     * 监听器触发回调方法
     * @param delegateTask
     */

    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("----->自定义的监听器执行了");
        if(EVENTNAME_CREATE.equals(delegateTask.getEventName())){
            //表示是Task的创建事件被触发了

            //1.指定当前Task节点的处理人
            delegateTask.setAssignee("wangwu");
        }
    }
}
