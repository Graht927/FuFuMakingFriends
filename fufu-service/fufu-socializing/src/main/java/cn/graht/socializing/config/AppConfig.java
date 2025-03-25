package cn.graht.socializing.config;

import cn.graht.socializing.utils.UserToolUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author GRAHT
 */

@Configuration
public class AppConfig {
    @Bean
    public UserToolUtils userToolUtils() {
        return new UserToolUtils();
    }
}
