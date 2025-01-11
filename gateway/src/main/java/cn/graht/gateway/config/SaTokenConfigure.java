package cn.graht.gateway.config;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GRAHT
 */
@Configuration
public class SaTokenConfigure {
    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
            // 拦截地址 
            .addInclude("/**")    /* 拦截全部path */
            // 开放地址 
            .addExclude("/favicon.ico")
            // 鉴权方法：每次访问进入 
            .setAuth(obj -> {
                // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录 
                SaRouter.match("/**")
                        .notMatch("/user-service-test/test/login")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //user服务
                        .notMatch("/user-service-test/doc.html")
                        .notMatch("/user-service-test/swagger-ui/**")
                        .notMatch("/user-service-test/webjars/**")
                        .notMatch("/user-service-test/v3/api-docs/**")
                        .notMatch("/user-service-test/v3/api-docs")
                        //order服务
                        .notMatch("/order-service-test/doc.html")
                        .notMatch("/order-service-test/swagger-ui/**")
                        .notMatch("/order-service-test/webjars/**")
                        .notMatch("/order-service-test/v3/api-docs/**")
                        .notMatch("/order-service-test/v3/api-docs")
                        .check(r->StpUtil.checkLogin());
//                 权限认证 -- 不同模块, 校验不同权限
                SaRouter.match("/user-service-test/**")
                        .notMatch("/user-service-test/test/login")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //user服务
                        .notMatch("/user-service-test/doc.html")
                        .notMatch("/user-service-test/swagger-ui/**")
                        .notMatch("/user-service-test/webjars/**")
                        .notMatch("/user-service-test/v3/api-docs/**")
                        .notMatch("/user-service-test/v3/api-docs")

                        .check(r -> StpUtil.checkPermission("user"));
                SaRouter.match("/order-service-test/**")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //user服务
                        .notMatch("/order-service-test/doc.html")
                        .notMatch("/order-service-test/swagger-ui/**")
                        .notMatch("/order-service-test/webjars/**")
                        .notMatch("/order-service-test/v3/api-docs/**")
                        .notMatch("/order-service-test/v3/api-docs")
                        .check(r -> StpUtil.checkPermission("order"));
                // 更多匹配 ...  */
            })
            // 异常处理方法：每次setAuth函数出现异常时进入 
            .setError(e -> {
                e.printStackTrace();
                return SaResult.error(e.getMessage());
            })
            ;
    }

    @Bean
    @ConditionalOnMissingBean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
