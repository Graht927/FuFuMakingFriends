package cn.graht.user;

import cn.dev33.satoken.SaManager;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("cn.graht.user.mapper")
@SpringBootApplication
public class FuFuUserApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(FuFuUserApplicationStarter.class,args);
        System.out.println(SaManager.getConfig());
    }
}