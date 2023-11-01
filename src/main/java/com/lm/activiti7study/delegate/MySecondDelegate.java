package com.lm.activiti7study.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.time.LocalDateTime;

public class MySecondDelegate implements JavaDelegate {
    /**
     *
     * @param delegateExecution
     */
    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("边界事件的通知任务执行...."+ LocalDateTime.now().toString());
    }
}
