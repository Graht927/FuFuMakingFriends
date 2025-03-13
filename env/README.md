##### 启动说明
###### 前提: config 下的FuFuMakingFriend目录下的配置文件已经修改完成 解压环境文件 自行下载apache-skywalking-apm-10.1.0 和 nacos2.3.2
###### 1、打开自己的mysql 数据库, 创建数据库 创建 nacos_config
###### 2、修改nacos2.3.2下的conf/application.properties文件修改本地数据库密码
###### 3、use到nacos_config数据库，执行nacos2.3.2/conf/mysql-schema.sql
###### 4、启动nacos2.3.2
###### 5、根据config下的 .metadata.yaml 文件，导入【config目录下】FuFuMakingFriend目录下的配置文件
###### 6、进入apache-skywalking-apm-10.1.0/bin目录下，执行startup.bat
###### 7、启动env目录下sentinel.bat
###### 8、启动自己在FuFuMakingFriend目录中配置的redis
###### 9、启动FuFuMakingFriend目录中配置的rocketmq nameServer和broker都需要启动 以及dashboard
###### 10、项目中的mysql是一主一从所以 你这里最好也是
###### 11、去查看根目录的README.md