package cn.graht.organizeBureau.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 *
 * @author GRAHT
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有请求路径跨域访问
                .allowCredentials(true) // 是否携带Cookie，默认false
                .allowedHeaders("*") // 允许的请求头类型
                .maxAge(3600)  // 预检请求的缓存时间（单位：秒）
                .allowedMethods("*") // 允许的请求方法类型
//                .allowedOrigins("http://localhost:8080") // 允许哪些域名进行跨域访问
                .allowedOriginPatterns("*");
    }
}