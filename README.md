## 说明文档
> 这个项目是一个基于SpringCloudAlibaba 实现的社交系统
#### 技术类型:
    1. SpringCloud -- 2023.0.1
    2. SpringCloudAlibaba -- 2023.0.1.0
    3. SpringBoot -- 3.3.5
    4. SaToken -- 1.39.0
    5. Skywalking -- 10.1.0
    6. knife4j -- 4.4.0
    7. nacos --  2.2.3
    8. mysql --8.0.23
#### 功能实现: 
>   待补充
#### 服务启动
##### 前提已经阅读了config目录下的README.md 以及 env目录下的README.md
##### 将sql目录下的sql文件导入到数据库中
##### 依次启动服务
###### :
```shell
fufu-gateway
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-gateway -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms128m -Xmx256m -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=256m -XX:+UseG1GC
fufu-mq-consumer
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-mq-consumer -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms128m -Xmx256m -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=128m -XX:+UseG1GC
fufu-mq-producer
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-mq-producer -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms128m -Xmx256m -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=128m -XX:+UseG1GC
fufu-organizer-bureau
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-organize-bureau -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms256m -Xmx512m -XX:InitialCodeCacheSize=128m -XX:ReservedCodeCacheSize=256m -XX:+UseG1GC
fufu-socializing
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-socializing -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms256m -Xmx512m -XX:InitialCodeCacheSize=128m -XX:ReservedCodeCacheSize=256m -XX:+UseG1GC
fufu-user
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-user -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms256m -Xmx512m -XX:InitialCodeCacheSize=128m -XX:ReservedCodeCacheSize=256m -XX:+UseG1GC
fufu-sms
	-javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-agent-9.3.0\skywalking-agent.jar -Dskywalking.agent.service_name=fufu-sms -Dskywalking.collector.backend_service=127.0.0.1:11800 -Xms128m -Xmx256m -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=256m -XX:+UseG1GC
```
###### 参数说明
> -javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-java-agent-9.3.0\skywalking-agent.jar [skywalking-agent jar包位置]
> -Dskywalking.agent.service_name=[服务名] -Dskywalking.collector.backend_service=127.0.0.1:11800 [日志地址]
