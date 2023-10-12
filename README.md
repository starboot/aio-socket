<p align="center">
	<img alt="logo" src="https://gitee.com/mxd_2022/static/raw/master/aio-socket/aio-socket-logo.png">
</p>
<h2 align="center" style="margin: 30px 0 30px; font-weight: bold;">aio-socket v3.x</h2>
<h4 align="center">一款强大且轻量的传输层通讯框架内核</h4>
<p align="center">
    <a href="https://www.oscs1024.com/project/oscs/mxd888/aio-socket?ref=badge_small"><img 
            src="https://www.oscs1024.com/platform/badge/mxd888/aio-socket.svg?size=small" 
            alt="OSCS Status"/></a>
	<a href="https://gitee.com/mxd_2022/aio-socket/stargazers"><img
            src="https://gitee.com/mxd_2022/aio-socket/badge/star.svg?theme=dark"  alt="gitee star"/></a>
	<a href="https://gitee.com/mxd_2022/aio-socket"><img 
	        src="https://img.shields.io/badge/aio--socket-2.0.0--RELEASE-yellowgreen"  alt=""/></a>
    <a href="https://www.apache.org/licenses/LICENSE-2.0"><img 
            src="https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg" alt=""/></a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html"><img 
            src="https://img.shields.io/badge/JDK-8+-green.svg"  alt=""/></a>
    <a target="_blank" href='https://github.com/mxd888/aio-socket'><img 
            src="https://img.shields.io/github/stars/mxd888/aio-socket.svg?style=social" alt="github star"/>
    </a>
</p>

## aio-socket 简介

aio-socket是Java AIO通讯内核，采用了内存池、线程池、插件化增添模块等思想进行设计。
采用静态类方法API进行统一封装，提供了基本的HTTP、WebSocket和MQTT等解编码能力。
意在制作一个易于理解、性能强悍的通讯内核。
为即时通讯(IM)、物联网(IOT)以及实时流媒体服务器(Real time streaming server)等领域提供价值吧！

## 初衷

致传输层Java通讯框架小白：<br/>
aio-socket并不是凭空而来，也不是多么优秀的设计。
GitHub有Netty和Mina等等项目。
国内也有相关的优秀产品，如t-io、smart-socket、TouchSocket、workflow、Voovan、
getty、SuperSocket、zinx、evpp、libtnet以及HP-Socket等等优秀的开源产品。
我们在使用这些优秀产品的前提就是学习《XX权威指南》并且读源代码。
留下的只有感叹，如此高深造诣。
我原本只是想学习前人优秀的设计思想，以及代码书写规范。当代码阅读到一定次数，产生了抄作业的想法。
于是aio-socket诞生啦！在学术界使用别人99%的已有成果，提升0.001%的性能便是一篇改进型论文。
所以面对创新，我们只能算改进吧！



## 能力

下述列出了aio-socket本领，并且友好的支持自定义扩展。
> 1. 内核级集群（去中心化分布式集群）
> 2. 断线重连功能
> 3. 内核级ACK消息确认（保证消息必达）
> 4. 插件化管理功能模块
> 5. 优秀的内存池模型
> 6. 全方面的流量监控
> 7. 质简质朴的架构设计
> 8. 一目了然的常用API封装
> 9. 无与伦比的半包、粘包处理
> 10. SSL、TLS加密模块
> 11. 禁止连接黑名单模块
> 12. 心跳检测（自动剔除60s无心跳的连接）
> 13. 完全调用底层jdk，无任何依赖，充分发挥jdk的性能
> 14. 5G时代、稳定、实时、亚毫秒级、百万并发级的TCP协议底层通讯内核
> 15. 基于aio-socket实现的产品：t-im(即时通讯框架)、WeChat(即时通讯安卓客户端)

## 性能
### 硬件水平

|  **指 标**         | **设 置** |  
| ---               | ---    |  
| **Hardware**      |      |  
| CPU               | Intel(R) Core(TM) i7-9700U |  
| Frequency         | 3.0 GHz |
| RAM               | 16 GB |
| Hard drive        | 2 TB |
| Network           | 千兆 |
| **Software**      |      |
| Operation System  | Windows 10        |
| openjdk version   | 1.8.0_311         |
| maven version     | 3.6.1             |
| IDE               | IDEA 2020.1 社区版 |

### 性能表现

这里给出平均数据，峰值流量目前出现过 **(570+)MB/s**, (**每5秒作为一个间隔**), 其中**count**表示次数，或者条数。<br/>
本实验不针对消息处理速度进行评比，因为毫无意义。你若非问消息处理性能，目前可以跑出2.69656674E7条，同等于2696w/s<br/>

|  指 标     |   表 现     |  
|  ---     |     ---      |  
|  输入流量  | 2727.97(**MB**) |  
|  输出流量  | 2728.25(**MB**) | 
|  IO网口读  | 21827(**count**) |  
|  IO网口写  | 708829(**count**) | 
|  在线人数  | 10 |  
|  连接总数  | 10 | 
|  消息处理量  | 6219700(**count/s**) |  
|  流量读取速率  | 545.5958(**MB/s**) | 

## 项目目录结构
```
aio-socket
├─doc 项目开发文档
├─aio-socket-codec-http  HTTP解编码
│    ├─aio-socket-http-client  HTTP客户端
│    ├─aio-socket-http-common  HTTP公共解编码
│    ├─aio-socket-http-parent  HTTP依赖管理
│    └─aio-socket-http-server  HTTP服务器
├─aio-socket-codec-mqtt  MQTT解编码
├─aio-socket-codec  基础解编码
│    └─main  核心源码总目录
│        └─java  
│           └─cn
│               └─starboot
│                   └─socket
│                       └─codec
│                           ├─base64    base64解编码
│                           ├─bytes     字节流解编码
│                           ├─protobuf  protobuf解编码
│                           ├─string    字符串解编码
│                           └─util      解编码工具包
├─aio-socket-demo  Demo代码目录
│    └─main  核心源码总目录
│        ├─java  
│        │  └─cn
│        │      └─starboot
│        │          └─socket
│        │              └─demo
│        │                  ├─ack        ACK Demo
│        │                  ├─basic      hello world Demo
│        │                  ├─batch      性能压测 Demo
│        │                  ├─cache      缓存 Demo
│        │                  ├─heartbeat  心跳 Demo
│        │                  ├─http       HTTP Server Demo
│        │                  ├─mqtt       MQTT Server Demo
│        │                  ├─mutiproto  多协议 Demo
│        │                  ├─plugins    自定义插件 Demo
│        │                  └─reconnect  重连 Demo
│        └─resources
│            └─page
│                └─websocket.html        用于测试websocket协议的客户端
├─aio-socket-kernel   aio-socket内核程序
│    └─main  核心源码总目录
│        └─java  
│           └─cn
│              └─starboot
│                  └─socket
│                      ├─config     配置信息
│                      ├─core       核心代码
│                      ├─enums      内核枚举
│                      ├─exception  解编码异常
│                      ├─intf       对外接口
│                      ├─jdk        改进JDK默认方法
│                      ├─maintain   关系维护
│                      ├─plugins    抽象插件接口
│                      ├─udp        UDP
│                      └─utils      内核工具包
├─aio-socket-parent  aio-socket依赖管理
├─aio-socket-plugin  aio-socket插件实现类
│    └─main  核心源码总目录
│        └─java  
│           └─cn
│               └─starboot
│                   └─socket
│                       └─plugins
│                           ├─ACKPlugin             ACK插件
│                           ├─BlackListPlugin       黑名单插件
│                           ├─ClusterPlugin         集群插件(开发中)
│                           ├─HeartPlugin           心跳插件
│                           ├─MonitorPlugin         监控插件
│                           ├─ReconnectPlugin       重连插件
│                           ├─SSLPlugin             SSL插件
│                           ├─SocketOptionPlugin    SocketOption插件
│                           └─StreamMonitorPlugin   流控监测插件
└─aio-socket-utils  aio-socket工具包
     └─main  核心源码总目录
         ├─java  
         │  └─cn
         │      └─starboot
         │          └─socket
         │              └─utils
         │                  ├─cache     缓存工具包
         │                  ├─config    配置
         │                  ├─hutool    封装hutool
         │                  ├─json      封装JSON
         │                  ├─lock      并发锁结构
         │                  ├─page      分页查询
         │                  ├─pool      内存池、线程池
         │                  ├─queue     自定义并发队列结构
         │                  └─scanner   包扫描器
         └─resources
             ├─caffeine.properties      Caffeine配置信息
             └─j2cache.properties       j2cache配置信息
```


## 软件架构
给大家画个架构图🎉 
![Image text](https://gitee.com/mxd_2022/static/raw/master/aio-socket/aio-socket-frame.jpg)
<h5 align="center">单机服务器架构图</h5>


## 快速开始

### aio-socket 作者电脑环境

> 1. jdk 1.8.0_221
> 2. maven 3.6.1
> 3. IDEA 2020.1 社区版


### 下载项目

``` shell
git clone git@gitee.com:starboot/aio-socket.git
```

### 依赖引入

v3.0版本暂未推送至Maven中央仓库<br/>
等待多次测试表现没问题，可以上生产环境后将会推送至Maven中央仓库<br/>
大家可以fork到自己仓库并且pull到本地电脑运行体验。<br/>

+ Apache Maven
~~~
<dependency>
  <groupId>cn.starboot.socket</groupId>
  <artifactId>aio-socket-all</artifactId>
  <version>3.0.0</version>
</dependency>
~~~

+ Gradle(groovy)

```groovy
implementation group: 'cn.starboot.socket', name: 'aio-socket-all', version: '3.0.0'
```

or

```groovy
implementation 'cn.starboot.socket:aio-socket-all:3.0.0'
```

+ Gradle(Kotlin)

```kotlin
implementation("cn.starboot.socket:aio-socket-all:3.0.0")
```


### **Demo**
```java
// 创建消息包处理类
public class ServerHandler extends StringHandler {
	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {

		try {
			System.out.println("收到来自客户端" + channelContext.getRemoteAddress() + "的消息:" + packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 将消息回发给客户端
		return packet;
	}
}
```
```java
public class Server {

    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap("localhost", 8888, new ServerHandler());
        bootstrap.setMemoryPoolFactory(1024 * 64, 2, true)
                .setReadBufferSize(1024 * 4)
                .setWriteBufferSize(1024 * 2, 128)
                .start();
    }
}
```

```java
public class ClientHandler extends StringHandler {
	@Override
	public Packet handle(ChannelContext channelContext, StringPacket packet) {
		System.out.println("收到来自服务器的消息：" + packet.getData());
		return null;
	}
}
```
```java
public class Client {

    public static void main(String[] args) throws IOException {
        Packet demoPacket = new StringPacket("hello aio-socket");
		ClientBootstrap bootstrap = new ClientBootstrap("localhost", 8888, new ClientHandler());
		bootstrap.setBufferFactory(1024 * 16, 2, true)
				.setReadBufferSize(1024 * 4)
				.setWriteBufferSize(1024 * 2, 4);

		ChannelContext start = bootstrap.start();
		Aio.send(start, demoPacket);
    }
}
```
<br/>
观看控制台打印信息(Server和Client的都有哦!)  

### **流量转发速率**压力测试(尽情享受aio-socket内核性能)(**540+MB/s**)
这里使用**batch**，并且测试客户端是：**StreamClient.java**。测试服务器没有变化<br/>
请看清楚是**batch**不是**basic**<br/>
**basic:** hello world 级别的测试<br/>
**batch:** 压力测试，用于展示aio-socket性能表现<br/>
<br/>
由于多线程并发运行，然而aio-socket针对IO通讯做了众多优化以及封装。号称性能小怪兽，一旦运行，则人机为之颤抖。<br/>
导致电脑出现轻微卡顿属于正常现象，不必担心。在服务器控制面板观察几分钟性能表现。<br/>
便可以关闭项目，最好首先关闭客户端，在关闭服务器。<br/>
<br/>
<br/>
首先启动服务器: **aio-socket-demo**/src/main/java/cn.starboot.socket.demo.**batch**.**server**.**Server.java**<br/>
启动客户端：**aio-socket-demo**/src/main/java/cn.starboot.socket.demo.**batch**.**client**.**StreamClient.java**<br/>
<br/>
<br/>
打开服务器的控制台，可以查看当前流量传输速率。<br/>

### **消息处理速率**压力测试 (**2696w/s**)
这里使用**batch**<br/>，并且测试客户端是：**ProcessorClient.java**。测试服务器没有变化<br/>
请看清楚是**batch**不是**basic**<br/>
**basic:** hello world 级别的测试<br/>
**batch:** 压力测试，用于展示aio-socket性能表现<br/>
<br/>
由于多线程并发运行，然而aio-socket针对IO通讯做了众多优化以及封装。号称性能小怪兽，一旦运行，则人机为之颤抖。<br/>
导致电脑出现轻微卡顿属于正常现象，不必担心。在服务器控制面板观察几分钟性能表现。<br/>
便可以关闭项目，最好首先关闭客户端，在关闭服务器。<br/>
<br/>
<br/>
首先启动服务器: **aio-socket-demo**/src/main/java/cn.starboot.socket.demo.**batch**.server.Server.java<br/>
启动客户端：**aio-socket-demo**/src/main/java/cn.starboot.socket.demo.**batch**.client.**ProcessorClient.java**<br/>
<br/>
<br/>
打开服务器的控制台，可以查看当前流量传输速率。比较每秒处理多少消息比如4000w/s、2000w/s、1000w/s、500w/s，这些没有意义。
作为通讯内核，应该关注流量转发速率，消息处理速度在ISO第七层的应用层有众多因素掺杂。并且这不应该成为量化一个通讯内核好坏的指标。<br/>

## 参与贡献

aio-socket热烈欢迎对Java多线程开发、网络IO通讯有兴趣的开发者或学生参与到项目建设中来，
aio-socket鼓励大家对项目提出建设性建议，项目长期维护。
<br/>
本作者欢迎大家提交PR。在一起，就可以。让我们用平凡的生命创建非凡的贡献。开源不易，还望多多包涵！！！


## 联系方式

   官方QQ群号：867691377 
   <a target="_blank"  href="https://jq.qq.com/?_wv=1027&k=Gd6P6BcT">
   <img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="t-im" title="t-im"></a><br>

## 进群前先在github或gitee上star ★
   请看完再进群，进群是为了相互交流技术，共同学习进步，设置进群问题是防止有某云服务器销售人员，或其他乱发广告的进入；

## 重要说明 ☆☆☆

   第一本项目完全开源免费，可以拥有其任何使用权，但不代表可以做非法乱纪的事情，本项目以质朴质简思想开发，意在打造最易读懂源码为主，并不是以高深的设计哲学为主，
   可用作学习或不重要的作业使用！！！非常感谢配合

## 上层应用开源项目链接
   
   t-im：基于aio-socket开发的高性能IM通讯框架，官网：  https://gitee.com/starboot/t-im  <br>  
   WeChat：为t-im 提供基本的UI功能，官网：  https://gitee.com/starboot/we-chat  <br>  
   本项目如有侵犯到任何个人或组织的权益请联系邮箱：1191998028@qq.com （如有侵权请联系删除）
   
   
## LICENSE

~~~
   Copyright 2019-2022 The aio-socket Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
~~~