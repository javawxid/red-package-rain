# 高并发项目：百万级QPS压测红包雨系统
[![Page Views Count](https://badges.toozhao.com/badges/01GAXJSF6YFBCWG5CTMZQSFB4R/red.svg)](https://badges.toozhao.com/stats/01GAXJSF6YFBCWG5CTMZQSFB4R "页面访问的次数")
[![Author](https://img.shields.io/badge/Author-liaozhiwei-blue)](https://www.liaozhiwei.cn/ "Author:liaozhiwei")
[![WeChat](https://img.shields.io/badge/Wechat-java__wxid-blueviolet)](https://www.liaozhiwei.cn/%E5%BE%AE%E4%BF%A1.jpg "WeChat:java_wxid")
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html "Apache协议")
[![JDK-1.8](https://img.shields.io/badge/JDK-1.8+-green.svg)](https://www.oracle.com/java/technologies/downloads/ "JDK-1.8")
[![MySQL](https://img.shields.io/badge/%E6%95%B0%E6%8D%AE%E5%BA%93-MySQL-critical)](https://www.mysql.com/ "数据库：MySQL")
[![Redis](https://img.shields.io/badge/%E7%BC%93%E5%AD%98-Redis-9cf)](https://redis.io/ "缓存：Redis")
[![RocketMQ](https://img.shields.io/badge/%E6%B6%88%E6%81%AF%E4%B8%AD%E9%97%B4%E4%BB%B6-RocketMQ-ff69b4)](https://rocketmq.apache.org/zh/ "消息中间件：RocketMQ")
[![SpringCould](https://img.shields.io/badge/%E5%BE%AE%E6%9C%8D%E5%8A%A1%E6%9E%B6%E6%9E%84-Spring%20Cloud-important)](https://www.springcloud.cc/ "微服务架构：SpringCould")
[![Apache ShardingSphere](https://img.shields.io/badge/%E5%88%86%E5%BA%93%E5%88%86%E8%A1%A8-Apache%20ShardingSphere-yellow)](https://shardingsphere.apache.org/ "分库分表：Apache ShardingSphere")
![version](https://img.shields.io/badge/v1.0.0-version-purple/  "版本")

## 项目简介
本项目是一个针对百万级QPS压测的红包雨系统，旨在挑战和提升高并发处理能力。基于之前在公司开发的经验，项目经过优化和调整，移除了特定业务逻辑，专注于抢红包核心功能，作为高并发压测的Demo项目。

需要注意的是，代码可以不断优化，但实际想挑战百万级的QPS，部署的集群不可能只有我提供的3个节点，需要更高规格的服务器配置和更多的节点，在代码上我也尽可能的将性能不断的调优。

## 使用技术
### 服务器参数优化
- Spring Boot项目配置优化（HTTP协议下数据压缩、静态资源缓存、Tomcat线程池配置）
- 启动时间优化（延迟初始化Bean、spring-context-indexer功能、11项优化启动方案）
### 请求限流与安全
- 同一IP地址请求限流
- 网关请求校验防篡改
- 防止请求重复提交
- 同一用户请求限流
- 应用服务限流
### 池化技术
- Redis连接池
- MySQL连接池
- 对象池
- 线程池（自适应核心线程数、自定义拒绝策略、滑动时间窗口、性能监控、重试纪元）
### 弹性伸缩与负载均衡
- 阿里云CLB
- Nginx
### 高可用集群
- RedisCluster（高可用6个节点）
- MySQLCluster（三主六从半同步复制）
- RocketMQCluster（dledger高可用9个节点）
- 高可用Nacos集群（3节点Nginx高负载集群）
### 高性能
- 预计算和缓存预热：提前拆分好红包金额到缓存中，待定时任务触发Redis广播消息发送WebSocket异步消息到客户端，客户端拿到活动参数开始抢红包，减少实时计算资源损耗。
### 其他技术
- Redis分布式锁
- 防止消息重复消费
- 多消费组多消费者消费
- 高并发请求合并
- Redis批处理
- MySQL批处理
- 设计模式（模板方法模式、代理模式、享元模式）
- 不可逆加密存储
- 用户请求鉴权（JWT）
- 多数据源管理
- Sharding-JDBC分库分表
- 子表批量生成
- 并发队列
- 分批处理
## 软件架构
- `red-package-rain-api`：抢红包服务
- `red-package-rain-server`：消费红包服务
- `spring-cloud-gateway`：服务网关，过滤请求限流用户
- `spring-cloud-security-oauth2`：服务鉴权
- `user`：处理用户请求服务
## 安装教程
1. **环境要求**：
  - JDK 1.8+
  - Redis集群
  - MySQL集群
  - RocketMQ集群
2. **部署环境**：
  - CentOS 7
  - Docker
  - Docker-Compose
3. **注意事项**：
  - 如果上述环境未安装配置，可下载资料中的CentOS镜像，内含预配置的集群环境。
  - 全部运行起来较耗内存，建议内存不低于64G。


## 使用说明
1. 先启动 `spring-cloud-security-oauth2` 服务。
2. 然后启动 `spring-cloud-gateway` 服务。
3. 其他服务启动顺序随意。


## 参与贡献
- 廖志伟

#### 操作步骤

这里以接口调用顺序进行描述：

##### 1.用户服务

批量创建用户任务（每秒一万用户，写入DB并保存用户名和密码到脚本中）：

http://localhost:8037/userinfo/startCreateUserTask


关闭创建用户的任务：

http://localhost:8037/userinfo/stopCreateUserTask

写入脚本文件（将用户id、登录的token、时间戳、验签写入脚本）：

http://localhost:8037/userinfo/writeScriptFile

##### 2.红包服务

添加红包活动（后台管理系统配置红包活动）：

http://localhost:8057/redPackageRainApi/addRedPackageActivity

抢红包：

http://localhost:8087/redPackageRainApi/snatchRedPackage

合并请求抢红包：

http://localhost:8057/redPackageRainApi/snatchRedPackageMergeRequest


#### 想说的话

目前这个项目还在不断调试中，视频也是录制一段之后，继续调整代码，项目中还有很多技术点没实现的，也不用着急，后续会慢慢添加上去的。

如果你对这个项目感觉还不错的话，欢迎留下一个Star，让更多人看到这个项目进行学习。


#### 资料


通过百度网盘分享的文件：资料
链接：https://pan.baidu.com/s/1mbG_id1Lm_aKu3AlfIidgA?pwd=2024
提取码：2024
--来自百度网盘超级会员V1的分享

#### 视频解说
哔哩哔哩视频地址：https://space.bilibili.com/353586723/channel/collectiondetail?sid=2589561


#### 部署说明

Node0节点：
ip地址：192.168.80.100
用户名：root 
密码：admin
项目自启动目录：/opt/app

Node1节点：
ip地址：192.168.80.101
用户名：root
密码：admin
集群部署目录：/opt/software

Node2节点：
ip地址：192.168.80.102
用户名：root
密码：admin
集群部署目录：/opt/software

Node3节点：
ip地址：192.168.80.103
用户名：root
密码：admin
集群部署目录：/opt/software

浏览器访问nacos地址：http://192.168.80.101:1848/nacos
用户名：nacos
密码：nacos

浏览器访问RocketMQ地址：http://192.168.80.103:19081/#/cluster

