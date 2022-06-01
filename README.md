 <p align="center">
      <img src="https://img.shields.io/badge/Release-V1.0.0-green.svg" alt="Downloads">
      <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" alt="Build Status">
  <img src="https://img.shields.io/badge/license-Apache%202-blue.svg" alt="Build Status">
   <img src="https://img.shields.io/badge/Spring%20Cloud-Hoxton.SR9-blue.svg" alt="Coverage Status">
   <img src="https://img.shields.io/badge/Spring%20Boot-2.3.5-blue.svg" alt="Downloads">
   <img src="https://img.shields.io/badge/Mybatis%20Plus-3.5.1-blue.svg" alt="Downloads">
   <img src="https://img.shields.io/badge/Postgresql-42.1.1-blue.svg" alt="Downloads">
   <img src="https://img.shields.io/badge/SkyWalking-8.7.0-blue.svg" alt="Downloads"> 
   <img src="https://img.shields.io/badge/Rocketmq-2.0.3-blue.svg" alt="Downloads"> 
   <img src="https://img.shields.io/badge/Elasticsearch-7.svg" alt="Downloads">
   <img src="https://img.shields.io/badge/Author-H%20zhi-ff69b4.svg" alt="Downloads">
 </p>  

## SpringLayer
* 采用前后端分离的模式，前端 (基于 Vue、Element-UI)。
* 后端采用SpringCloud全家桶，并同时对其基础组件做了高度的封装，单独出一个框架：[LayerTool]。
* [LayerTool]已推送至中央仓库仓库，直接引入即可，减少了工程的臃肿，也可更注重于业务开发。
* 集成Sentinel从流量控制、熔断降级、系统负载等多个维度保护服务的稳定性。
* 注册中心、配置中心选型Nacos，为工程瘦身的同时加强各模块之间的联动。
* 极简封装了SysServer底层，用更少的代码换来拓展性更强的ACL系统。
* 整合OAuth2，实现了终端认证系统，可控制系统的token权限互相隔离，达到接口级别认证。
* 整合Security，封装了Secure模块，采用JWT做Token认证，可拓展集成Redis等细颗粒度控制方案。
* 集成Flowable工作流引擎，自定义页面，API接口开发，流程快速定制。
* 封装RocketMq事件流消息，封装了RocketMq模块，高性能数据管道、流分析、数据集成、服务间削峰、解耦、异步。
* 封装OSS高性能对象存储服务器。支持公有、私有化部署。海量、安全、低成本、高可靠的云存储服务，存放任意类型的文件。容量和处理能力弹性扩展，多种存储类型供选择。
* 封装Elasticsearch组件、分布式、RESTful 风格的搜索和数据分析引擎，支持多服务聚合索引查询，提升查询响应等。
* 整合Skywalking支持可视化告警、支持JVM监控、支持全局调用统计等等。
* fat jar -> docker -> k8s + jenkins部署架构。
* 项目分包明确，规范微服务的开发模式，使包与包之间的分工清晰。

## 架构图
<img src="http://192.168.1.254/zkzx-layer/layer-core/-/raw/master/%E6%9E%B6%E6%9E%84%E5%9B%BE.png"/>

## 服务工程结构
``` 
LayerServer
├── layer-client -- Feign服务提供
├── layer-common -- 常用工具封装包
├── src -- 业务服务
├    ├── application -- 应用层
├    ├── controller -- 控制器
├    ├── engine -- 原生层
├    ├── ├── components -- 组件层
├    ├── ├── ├── redis -- 缓存层
├    ├── ├── ├── mq -- 消息层
├    ├── ├── domain -- 领域层
├    ├── ├── ├── beans -- 内部包装
├    ├── ├── ├── constants -- 领域常量
├    ├── ├── ├── convertot -- 转换器
├    ├── ├── ├── core -- 领域值对象
├    ├── ├── ├── enums -- 领域枚举
├    ├── ├── ├── service -- 细粒度逻辑
├    ├── ├── mapper -- ORM映射层
├    ├── ├── service -- 粗粒度逻辑
├    ├── ├── utils -- 工具类
├    ├── feign -- Feign实现
├    ├── infrastructure -- 组件层
├    ├── ├── application -- 组件应用层
├    ├── ├── exception -- 异常包装
├    ├── ├── listener -- 消息监听
├    ├── interfaces -- 抽象层
├    ├── ├── adaptors -- 适配器
├    ├── ├── service -- 抽象逻辑
└──  └──support -- 远程支持转换
```


## 在线演示
* Kibana-数据可视化和挖掘工具：[http://192.168.1.253:5601/app/kibana](http://192.168.1.253:5601/app/kibana)
* Jenkins-CICD：[http://192.168.1.253:8080/login](http://192.168.1.253:8080/login)
* Nexus-私服依赖库：[http://192.168.1.253:8081/]http://192.168.1.253:8081/)
* SonarQube 代码缺陷检测：[http://192.168.1.253:9000/](http://192.168.1.253:9000/)
* Rocketmq 消息可视化：[http://192.168.1.253:18080/#/](http://192.168.1.253:18080/#/)
* Nacos 服务发现和配置中心：[http://192.168.1.253:8848/nacos/index.html#/login](http://192.168.1.253:8848/nacos/index.html#/login)

## 技术文档
* [常见问题集锦]()
* [开发手册一览]()
* [基于Kuboard部署K8S]()

## 项目地址
* 后端Gitlib地址：[]()
* 前端框架(基于Vue)：[]()
* 核心框架项目地址：[]()

# 协议
Apache Licence 2.0 （[英文原文](http://www.apache.org/licenses/LICENSE-2.0.html)）
Apache Licence是著名的非盈利开源组织Apache采用的协议。该协议和BSD类似，同样鼓励代码共享和尊重原作者的著作权，同样允许代码修改，再发布（作为开源或商业软件）。
需要满足的条件如下：
* 需要给代码的用户一份Apache Licence
* 如果你修改了代码，需要在被修改的文件中说明。
* 在延伸的代码中（修改和有源代码衍生的代码中）需要带有原来代码中的协议，商标，专利声明和其他原来作者规定需要包含的说明。
* 如果再发布的产品中包含一个Notice文件，则在Notice文件中需要带有Apache Licence。你可以在Notice中增加自己的许可，但不可以表现为对Apache Licence构成更改。
Apache Licence也是对商业应用友好的许可。使用者也可以在需要的时候修改代码来满足需要并作为开源或商业产品发布/销售。

# 界面

## [Flowable](#) 工作流一览


## [Knife4j](#) 界面一览

