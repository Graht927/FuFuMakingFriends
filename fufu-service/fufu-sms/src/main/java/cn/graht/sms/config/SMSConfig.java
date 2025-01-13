package cn.graht.sms.config;

import cn.graht.utils.aliSendSMS.SMSParams;
import cn.graht.utils.aliSendSMS.SMSTemplateCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */
@Configuration
@Slf4j
public class SMSConfig {
    @Value("${ali.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${ali.sms.accessSecret}")
    private String accessSecret;
    @Value("${ali.sms.signName}")
    private String signName;
    @Value("${ali.sms.loginTemplateCode}")
    private String loginTemplateCode;
    @Value("${ali.sms.registerTemplateCode}")
    private String registerTemplateCode;
    @Value("${ali.sms.reqHeaderCode}")
    private String reqHeaderCode;

    @Bean
    public SMSParams smsParams() {
        SMSParams smsParams = new SMSParams();
        smsParams.setAccessKeyId(accessKeyId);
        smsParams.setAccessSecret(accessSecret);
        smsParams.setSignName(signName);
        smsParams.setReqHeaderCode(reqHeaderCode);
        return smsParams;
    }

    @Bean
    public SMSTemplateCode smsTemplateCode(){
        SMSTemplateCode smsTemplateCode = new SMSTemplateCode();
        smsTemplateCode.setLoginTemplateCode(loginTemplateCode);
        smsTemplateCode.setRegisterTemplateCode(registerTemplateCode);
        return smsTemplateCode;
    }
}
