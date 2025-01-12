package cn.graht.user.config;

import cn.graht.utils.aliSendSMS.SMSParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */
@Configuration
public class AppConfig {
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);
    @Value("${ali.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${ali.sms.accessSecret}")
    private String accessSecret;
    @Value("${ali.sms.signName}")
    public String signName;

    @Bean
    public SMSParams smsParams(){
        SMSParams smsParams = new SMSParams();
        smsParams.setAccessKeyId(accessKeyId);
        smsParams.setAccessSecret(accessSecret);
        smsParams.setSignName(signName);
        log.info("{}",smsParams);
        return smsParams;
    }
}
