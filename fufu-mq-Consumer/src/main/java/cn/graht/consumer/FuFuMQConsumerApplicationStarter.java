package cn.graht.consumer;

import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author GRAHT
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients("cn.graht.feignApi")
@ImportAutoConfiguration({RocketMQAutoConfiguration.class})
public class FuFuMQConsumerApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(FuFuMQConsumerApplicationStarter.class, args);
    }

}
