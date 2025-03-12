package cn.graht.producer.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  redisson配置
 * @author grhat
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {

    private String host;
    private String port;
    private String password;

    @Bean
    public Redisson redisson(){
        Config config = new Config();
        if (StringUtils.isNotBlank(password)){
            config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port)).setPassword(password);
        }else config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port));
        return (Redisson) Redisson.create(config);
    }
}
