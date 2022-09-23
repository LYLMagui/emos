# 在线办公小程序项目

[项目介绍](#项目介绍)

[项目功能演示](#项目功能演示)

[目录结构](#目录结构)

[技术选型](#技术选型)

[开发环境](#开发环境)


# 项目介绍

这是一款基于SpringBoot的企业在线办公系统。系统融合了诸多会议软件、打卡软件的优点，实现企业人员的在线人脸签到、考勤统计、消息收发、会议管理（未开发完）等功能。



## 项目功能演示

### 登录页
<img src="https://user-images.githubusercontent.com/74690360/191976885-95e15db0-62d2-4545-88c2-3787b8f1c312.png"  height="700" alt="登录页"/><br/>





### 注册页

<img src="https://user-images.githubusercontent.com/74690360/191978161-0eae903b-4732-4259-a489-648f843dc987.png"  height="700" alt="注册页"/><br/>


### 主页

<img src="https://img-blog.csdnimg.cn/610694b87482442d971e7aacc7ed0199.png"  height="700"/><br/>


### 人脸签到

<img src="https://img-blog.csdnimg.cn/50c13709c1b248079fd1bf1d06498f30.png"  height="700"/><br/>


### 考勤统计
模拟高风险地区签到，并且发送邮件告知人事部门

<img src="https://img-blog.csdnimg.cn/bf67b0d9402a4bf3ad3f55b576dcd14e.png"  height="700"/><br/>
<img src="https://img-blog.csdnimg.cn/21b23ad710144175bccb488651dab5ac.png"  with="700"/><br/>



### 月考勤统计

<img src="https://img-blog.csdnimg.cn/74f462d5e02148eb860f90d5cbbe4d6b.png"  height="700"/><br/>

### 消息系统
登录系统时，会接收到系统发送的消息

<img src="https://img-blog.csdnimg.cn/c41b1961c9e649888cc80d9336a3323f.png"  height="700"/><br/>


点击消息提醒可以查看消息列表

<img src="https://img-blog.csdnimg.cn/8bade7610eaa4181b96ebf47bcecfad1.png"  with="360"/><br/>

<img src="https://img-blog.csdnimg.cn/6c4701643d3f4a88aba08c43c2ee632e.png"  height="700"/><br/>


可以查看消息的详细信息

<img src="https://img-blog.csdnimg.cn/a5161294a4a1440c97bd543a0bac626e.png"  with="360"/><br/>


查看过的消息会变为已读状态

<img src="https://img-blog.csdnimg.cn/851fc448ddbf47088ffc74f37c47712c.png"  with="360"/><br/>




消息可以删除

<img src="https://img-blog.csdnimg.cn/68502361310b4a8887efc17da938a8b8.png"  with="360"/><br/>

<img src="https://img-blog.csdnimg.cn/e2165f43b5bf4345bb31f7b5f7a8fc8b.png"  with="360"/><br/>


# 目录结构
```sql
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─ukir
│  │  │          └─emos
│  │  │              └─wx
│  │  │                  ├─aop 
│  │  │                  ├─common      -- 工具
│  │  │                  │  └─util
│  │  │                  ├─config      -- 配置文件
│  │  │                  │  ├─shiro
│  │  │                  │  └─xss
│  │  │                  ├─controller  -- 控制器
│  │  │                  │  └─form
│  │  │                  ├─db          -- 数据层
│  │  │                  │  ├─dao
│  │  │                  │  └─pojo
│  │  │                  ├─exception   -- 自定义异常
│  │  │                  ├─service     -- 业务层
│  │  │                  │  └─Impl   
│  │  │                  └─task        
│  │  └─resources
│  │      └─mapper
│  └─test
└─target
   
```

# 技术选型

## 后端技术

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




## 前端技术
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

# 开发环境
|工具|版号|
|--|--|
|JDK|1.8|
|Mysql|8.0.28|
|Redis|6.0|
|RabbitMQ|3.10.5|
|Linux系统|Centos7.X|
|Docker|1.13.1|




