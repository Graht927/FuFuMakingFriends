package cn.graht.sms.config;

import cn.graht.sms.interceptor.SMSInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author GRAHT
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SMSInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
