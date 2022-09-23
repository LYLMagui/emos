# emos
在线办公小程序项目

@[TOC](目录)
## 项目介绍

这是一款基于SpringBoot的企业在线办公系统。系统融合了诸多会议软件、打卡软件的优点，实现企业人员的在线人脸签到、考勤统计、消息收发、会议管理（未开发完）等功能。



### 项目功能演示

#### 登录页
![在这里插入图片描述](https://img-blog.csdnimg.cn/e37397068b034316ada12dfa95a596f7.png =300x)



#### 注册页
![在这里插入图片描述](https://img-blog.csdnimg.cn/ee70359c6c5948f1a501fdd7d4699864.png  =300x)

#### 主页
![在这里插入图片描述](https://img-blog.csdnimg.cn/610694b87482442d971e7aacc7ed0199.png =300x)
#### 人脸签到
![在这里插入图片描述](https://img-blog.csdnimg.cn/50c13709c1b248079fd1bf1d06498f30.png =300x)
#### 考勤统计
模拟高风险地区签到，并且发送邮件
![在这里插入图片描述](https://img-blog.csdnimg.cn/bf67b0d9402a4bf3ad3f55b576dcd14e.png =300x)
![在这里插入图片描述](https://img-blog.csdnimg.cn/21b23ad710144175bccb488651dab5ac.png )



#### 月考勤统计
![在这里插入图片描述](https://img-blog.csdnimg.cn/74f462d5e02148eb860f90d5cbbe4d6b.png =300x)

#### 消息系统
登录系统时，会接收到系统发送的消息
![在这里插入图片描述](https://img-blog.csdnimg.cn/c41b1961c9e649888cc80d9336a3323f.png =300x)



点击消息提醒可以查看消息列表
![在这里插入图片描述](https://img-blog.csdnimg.cn/8bade7610eaa4181b96ebf47bcecfad1.png =300x)

![在这里插入图片描述](https://img-blog.csdnimg.cn/6c4701643d3f4a88aba08c43c2ee632e.png =300x)
可以查看消息的详细信息

![在这里插入图片描述](https://img-blog.csdnimg.cn/a5161294a4a1440c97bd543a0bac626e.png =300x)

查看过的消息会变为已读状态
![在这里插入图片描述](https://img-blog.csdnimg.cn/851fc448ddbf47088ffc74f37c47712c.png =300x)
![在这里插入图片描述](https://img-blog.csdnimg.cn/86d75bbfcc174be3a2cacba38ee9d474.png =300x)



消息可以删除
![在这里插入图片描述](https://img-blog.csdnimg.cn/68502361310b4a8887efc17da938a8b8.png =300x)

![在这里插入图片描述](https://img-blog.csdnimg.cn/e2165f43b5bf4345bb31f7b5f7a8fc8b.png =300x)


### 技术选型

#### 后端技术

| 技术       | 说明     |
| ---------- | -------- |
| SpringBoot | 容器+MVC |
|Shiro|认证和授权框架|
|MyBatis|ORM框架|
|RabbitMQ|消息中间件|
|Redis|缓存|
|MongoDB|NoSQL数据库|
|Docker|应用容器引擎|
|JWT|JWT登录支持|
|Hutool|Java工具类库|
|Lombok|简化对象封装工具|
|Kinfe4J|接口文档生成工具|




#### 前端技术
|技术|说明|
|--|--|
|UniApp|前端框架|
|Vue|前端框架|
|Ajax|前端HTTP框架|
### 环境搭建
**开发工具**
|工具|说明|
|--|--|
|IDEA|开发IDE|
|RedisDesktop|redis客户端连接工具|
|Navicat|数据库连接工具|
|HbuilderX|前端开发工具|
|VirtualBox|Linux虚拟机|
|MobaXterm|SSH连接工具|
|微信开发者工具|小程序调试模拟器|

开发环境
|工具|版号|
|--|--|
|JDK|1.8|
|Mysql|8.0.28|
|Redis|6.0|
|RabbitMQ|3.10.5|
|Linux系统|Centos7.X|
|Docker|1.13.1|



