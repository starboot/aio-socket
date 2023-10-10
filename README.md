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

Java AIO通讯内核，采用了内存池、线程池、插件化增添模块等思想进行设计，借鉴了前人的优秀设计
意在制作一个易于理解、性能强悍的通讯内核。为单机驾驭百万连接不懈努力。下述列出了aio-socket
本身本领，并且友好的支持自定义扩展。
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

## 软件架构
给大家画个架构图🎉 
![Image text](https://gitee.com/mxd_2022/static/raw/master/aio-socket/aio-socket-frame.jpg)
<h5 align="center">单机服务器架构图</h5>

## 开始使用
#### aio-socket 作者电脑环境
> 1. jdk 1.8.0_221
> 2. maven 3.6.1
> 3. IDEA 2020.1 社区版
#### 引入Maven坐标  
v3.0版本暂未推送至Maven中央仓库，大家可以fork到自己仓库并且pull到本地电脑运行体验。<br/>
首先启动服务器: aio-socket-demo/src/main/java/cn.starboot.socket.demo.batch.server.Server.java<br/>
启动客户端：aio-socket-demo/src/main/java/cn.starboot.socket.demo.batch.client.Client.java<br/>
<br/>
~~~
<dependency>
  <groupId>cn.starboot.socket</groupId>
  <artifactId>aio-socket-all</artifactId>
  <version>3.0.0</version>
</dependency>
~~~
#### 起步
aio-socket
## 联系方式

   官方QQ群号：867691377 
   <a target="_blank"  href="https://jq.qq.com/?_wv=1027&k=Gd6P6BcT">
   <img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="t-im" title="t-im"></a><br>

## 进群前先在github或gitee上star ★
   请看完再进群，进群是为了相互交流技术，共同学习进步，设置进群问题是防止有某云服务器销售人员，或其他乱发广告的进入；

## 重要说明 ☆☆☆

   第一本项目完全开源免费，可以拥有其任何使用权，但不代表可以做非法乱纪的事情，本项目以质朴质简思想开发，意在打造最易读懂源码为主，并不是以高深的设计哲学为主，
   可用作学习或不重要的作业使用，切勿商用，商用出现后果概不负责！！！非常感谢配合

## 上层应用开源项目链接
   
   t-im：基于aio-socket开发的高性能IM通讯框架，官网：  https://gitee.com/starboot/t-im  <br>  
   WeChat：为t-im 提供基本的UI功能，官网：  https://gitee.com/starboot/we-chat  <br>  
   本项目如有侵犯到任何个人或组织的权益请联系邮箱：1191998028@qq.com （如有侵权请联系删除）