package cn.graht.search.config;

import cn.graht.search.caffeine.CaffeineCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */
@Configuration
public class CaffeineConfig {
    @Bean
    public CaffeineCacheService caffeineCacheService() {
        return new CaffeineCacheService();
    }
}
