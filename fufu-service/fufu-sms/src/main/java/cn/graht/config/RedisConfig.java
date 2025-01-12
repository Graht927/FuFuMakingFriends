package cn.graht.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author GRAHT
 */
@Configuration
public class RedisConfig {
    @Bean(name = "encodedRedisTemplate")
    public RedisTemplate<String,Object> encodedRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 把默认的序列化器改成StringRedisSerializer
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setDefaultSerializer(stringSerializer);
        return redisTemplate;
    }
}
