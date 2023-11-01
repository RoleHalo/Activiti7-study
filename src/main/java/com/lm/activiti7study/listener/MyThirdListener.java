package com.lm.activiti7study.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import java.util.Map;
import java.util.Set;

/**
 * 测试局部和历史流程变量
 *      更改流程变量
 */

//此类定义的是用户进程监听器
public class MyThirdListener implements TaskListener {

    /**
     * 监听器触发回调方法
     * @param delegateTask
     */

    @Override
    public void notify(DelegateTask delegateTask) {

        System.out.println("----->MyThirdListener监听器执行了");
        if(EVENTNAME_CREATE.equals(delegateTask.getEventName())){
            //表示是Task的创建事件被触发了
            //获取流程变量
            Map<String, Object> variables = delegateTask.getVariables();
            Set<String> keySet = variables.keySet();
            for (String key : keySet) {
                Object obj = variables.get(key);
                System.out.println(key+"="+obj);
                //获取之后修改流程变量
             //   delegateTask.setVariable(key,obj+"[]");//有效操作√
            }
        }
    }
}
