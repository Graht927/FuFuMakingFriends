## 说明文档
> 这个项目是一个基于SpringCloudAlibaba的基础项目
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
>  用户服务 :
>  通过feign来远程调用
>  订单服务
#### 服务启动
> -javaagent:C:\Users\43070\Desktop\bs\Code\env\apache-skywalking-java-agent-9.3.0\skywalking-agent.jar [skywalking-agent jar包位置]
> -Dskywalking.agent.service_name=[服务名] -Dskywalking.collector.backend_service=127.0.0.1:11800 [日志地址]