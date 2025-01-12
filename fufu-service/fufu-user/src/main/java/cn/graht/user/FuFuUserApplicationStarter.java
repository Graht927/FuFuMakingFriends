package cn.graht.user;

import cn.graht.user.utils.SpringContextUtils;
import cn.graht.utils.aliSendSMS.SMSParams;
import cn.graht.utils.aliSendSMS.SMSTemplateCode;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("cn.graht.user.mapper")
@SpringBootApplication
public class FuFuUserApplicationStarter {
    private static final Logger log = LoggerFactory.getLogger(FuFuUserApplicationStarter.class);

    public static void main(String[] args) {
        SpringApplication.run(FuFuUserApplicationStarter.class,args);
        SMSTemplateCode bean = SpringContextUtils.getBean(SMSTemplateCode.class);
        log.info("{}",bean);
        SMSParams bean1 = SpringContextUtils.getBean(SMSParams.class);
        log.info("{}",bean1);
    }
}