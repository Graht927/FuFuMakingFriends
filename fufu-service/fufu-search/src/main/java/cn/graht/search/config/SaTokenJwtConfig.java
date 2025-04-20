package cn.graht.search.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author GRAHT
 */
@Configuration
public class SaTokenJwtConfig {
    @Value("${sa-token.redis.host}")
    private String host;
    @Value("${sa-token.redis.port}")
    private int port;
    @Value("${sa-token.redis.password}")
    private String password;
    @Value("${sa-token.redis.database}")
    private int database;
    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(password);
        config.setDatabase(database);  // 使用 db0
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();

    }
}
