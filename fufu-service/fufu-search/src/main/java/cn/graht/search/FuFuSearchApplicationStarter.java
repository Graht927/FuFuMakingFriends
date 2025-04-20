package cn.graht.search;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * @author GRAHT
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("cn.graht.feignApi")
@EnableCaching
public class FuFuSearchApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(FuFuSearchApplicationStarter.class,args);
    }
}