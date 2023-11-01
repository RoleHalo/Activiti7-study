# Activiti7-study
工作流-Activiti7学习过程代码整理
学习内容：（笔记地址：https://rolehalo.gitee.io/）
- [一、什么是工作流](#一什么是工作流)
- [二、Activiti7概述](#二activiti7概述)
  - [2.1 概述](#21-概述)
  - [2.2 使用](#22-使用)
  - [2.3 Activiti7开发环境](#23-activiti7开发环境)
    - [2.3.1 下载BPMN设计器](#231-下载bpmn设计器)
    - [2.3.2 数据库支持](#232-数据库支持)
- [三、SpringBoot+MySQL搭建activiti7入门案例](#三springbootmysql搭建activiti7入门案例)
  - [3.1 创建mysql中activiti数据库的数据库](#31-创建mysql中activiti数据库的数据库)
    - [3.1.1 引入activiti依赖](#311-引入activiti依赖)
    - [3.1.2 test下添加测试类`Activiti7Test01`](#312-test下添加测试类activiti7test01)
    - [3.1.3 配置数据库信息](#313-配置数据库信息)
    - [3.1.4 数据库自动生成表](#314-数据库自动生成表)
  - [3.2 创建BPMN流程图](#32-创建bpmn流程图)
  - [3.3 流程操作](#33-流程操作)
    - [3.3.1 流程部署](#331-流程部署)
    - [3.3.2 发起流程](#332-发起流程)
    - [3.3.3 查询流程](#333-查询流程)
    - [3.3.4 审批流程](#334-审批流程)
    - [3.3.5 涉及的表](#335-涉及的表)
- [四、任务分配](#四任务分配)
  - [4.1 固定分配](#41-固定分配)
  - [4.2 表达式](#42-表达式)
    - [4.2.1 值表达式](#421-值表达式)
    - [4.2.2 方法表达式](#422-方法表达式)
  - [4.3 监听器配置](#43-监听器配置)
- [五、流程变量](#五流程变量)
  - [5.1 运行时流程变量](#51-运行时流程变量)
    - [5.1.1 全局流程变量](#511-全局流程变量)
    - [5.1.2 局部流程变量](#512-局部流程变量)
  - [5.2 历史流程变量](#52-历史流程变量)
- [六、身份服务](#六身份服务)
  - [6.1 审批人](#61-审批人)
  - [6.2 候选人](#62-候选人)
  - [6.3 候选人组](#63-候选人组)
- [七、网关](#七网关)
  - [7.1 排他网关](#71-排他网关)
  - [7.2 并行网关](#72-并行网关)
  - [7.3 包容网关](#73-包容网关)
  - [7.4 事件网关](#74-事件网关)
- [八、 事件](#八-事件)
  - [8.1 定时器](#81-定时器)
    - [8.1.1 启动事件](#811-启动事件)
    - [8.1.2 中间事件](#812-中间事件)
    - [8.1.3 边界事件](#813-边界事件)
  - [8.2 消息事件](#82-消息事件)
    - [8.2.1 启动事件](#821-启动事件)
    - [8.2.2 中间事件](#822-中间事件)
    - [8.2.3 边界事件](#823-边界事件)
