package cn.graht.organizeBureau;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("cn.graht.feignApi")
@EnableCaching
@MapperScan("cn.graht.organizeBureau.mapper")
public class FuFuOrganizeBureauApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(FuFuOrganizeBureauApplicationStarter.class,args);
    }
}