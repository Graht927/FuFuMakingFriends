package cn.graht.socializing;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @author GRAHT
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("cn.graht.feignApi")
@EnableCaching
@MapperScan("cn.graht.socializing.mapper")
public class FuFuSocializingApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(FuFuSocializingApplicationStarter.class,args);
    }
}