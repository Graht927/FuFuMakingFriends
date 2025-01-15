package cn.graht.user;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@EnableFeignClients("cn.graht.feignApi")
@MapperScan("cn.graht.user.mapper")
@SpringBootApplication
@EnableTransactionManagement
public class FuFuUserApplicationStarter {
    private static final Logger log = LoggerFactory.getLogger(FuFuUserApplicationStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(FuFuUserApplicationStarter.class,args);
    }
}