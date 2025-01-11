package cn.graht.user.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */

@Configuration
public class SaTokenConfig {
    @Bean
    @ConditionalOnMissingBean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
