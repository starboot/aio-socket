
<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h2 align="center" style="margin: 30px 0 30px; font-weight: bold;">aio-socket 2.10.1.v20211002-RELEASE</h2>
<h4 align="center">一款强大且轻量的面向网络层通讯框架</h4>
<p align="center">
	<a href="https://gitee.com/mxd_2022/aio-socket/stargazers"><img
            src="https://gitee.com/mxd_2022/aio-socket/badge/star.svg?theme=dark"/></a>
	<a href="https://gitee.com/mxd_2022/aio-socket"><img src="https://img.shields.io/badge/aio--socket-2.10.1.v20211002--RELEASE-yellowgreen"></a>
    <a href="https://www.apache.org/licenses/LICENSE-2.0"><img src="https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg"/></a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html"><img src="https://img.shields.io/badge/JDK-8+-green.svg" /></a>
</p>

## aio-socket 简介

Java AIO通讯内核，采用了内存池、线程池、插件化增添模块等思想进行设计，借鉴了前人的优秀设计
意在制作一个易于理解、性能强悍的通讯内核。为单机驾驭百万连接不懈努力。下述列出了aio-socket
本身本领，并且友好的支持自定义扩展。
> 1. 内核级集群（去中心化分布式集群）
> 2. 断线重连功能
> 3. 内核级ACK消息确认（保证消息必达）
> 4. 插件化管理功能模块
> 5. 优秀的内存池管理
> 6. 全方面的流量监控
> 7. 质简质朴的架构设计
> 8. 一目了然的常用API封装
> 9. 无与伦比的半包、粘包处理
> 10. SSL、TLS加密模块
> 11. 禁止连接黑名单模块
> 12. 心跳检测（自动剔除60s无心跳的连接）
> 13. 完全调用底层jdk，无任何依赖，无任何jdk底层源码的增强
> 14. 5G时代、稳定、实时、亚毫秒级、百万并发级的TCP协议底层通讯内核
> 15. 基于aio-socket实现的产品：t-im(即时通讯框架)、WeChat(即时通讯安卓客户端)

## 软件架构
等有时间了给大家画个架构图🎉 

![Image text](https://gitee.com/mxd_2022/aio-socket/blob/master/images/aio-socket%20frame.jpg)
## 安装教程
aio-socket 作者电脑环境
> 1. jdk 1.8.0_221
> 2. maven 3.6.1
> 3. IDEA 2020.1 社区版
#### 引入Maven坐标  
~~~
<dependency>
  <groupId>io.github.mxd888.socket</groupId>
  <artifactId>aio-socket</artifactId>
  <version>2.10.1.v20211002-RELEASE</version>
</dependency>
~~~
## 联系方式

   官方QQ群号：867691377 
   <a target="_blank"  href="https://jq.qq.com/?_wv=1027&k=Gd6P6BcT">
   <img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="t-im" title="t-im"></a><br>

## 进群前先gitee上star ★
   请看完再进群，进群是为了相互交流技术，共同学习进步，设置进群问题是防止有某云服务器销售人员，或其他乱发广告的进入；

## 重要说明 ☆☆☆

   第一本项目完全开源免费，可以拥有其任何使用权，但不代表可以做非法乱纪的事情，本项目以质朴质简思想开发，意在打造最易读懂源码为主，并不是以高深的设计哲学为主，
   可用作学习或不重要的作业使用，切勿商用，商用出现后果概不负责！！！非常感谢配合

## 附上部分优秀开源项目链接
   
   t-im：由本人开发，基于aio-socket开发的高性能IM通讯框架，官网：  https://gitee.com/mxd_2022/t-im  <br>  
   WeChat：由本人开发，为t-im 提供基本的UI功能，官网：  https://gitee.com/mxd_2022/we-chat  <br>  
   t-io：稳如泰山，性能炸裂的Java通讯框架，官网：  https://www.tiocloud.com/tio/  <br>  
   Hutool：一个小而全的Java工具类库，官网：  https://www.hutool.cn/   <br>  
   smart-socket：高性能Aio通讯，官网： https://smartboot.gitee.io/book/smart-socket/    <br>  
   J-IM：高性能IM，官网（暂时打不来，或许作者正在开发中，尽情期待）：  http://www.j-im.cn/ <br>  
   本项目如有侵犯到任何个人或组织的权益请联系邮箱：1191998028@qq.com （如有侵权请联系删除）