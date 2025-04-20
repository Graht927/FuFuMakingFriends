package cn.graht.search.config;


import cn.graht.search.interceptor.TokenDelayRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */

@Configuration
public class AppConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new TokenDelayRequestInterceptor();
    }
}
