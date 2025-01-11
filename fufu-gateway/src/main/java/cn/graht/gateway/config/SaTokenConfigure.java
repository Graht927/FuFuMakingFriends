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
                        .notMatch("/fufu-user/v1/login auth")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //user服务
                        .notMatch("/fufu-user/doc.html")
                        .notMatch("/fufu-user/swagger-ui/**")
                        .notMatch("/fufu-user/webjars/**")
                        .notMatch("/fufu-user/v3/api-docs/**")
                        .notMatch("/fufu-user/v3/api-docs")
                        //socializing服务
                        .notMatch("/fufu-socializing/doc.html")
                        .notMatch("/fufu-socializing/swagger-ui/**")
                        .notMatch("/fufu-socializing/webjars/**")
                        .notMatch("/fufu-socializing/v3/api-docs/**")
                        .notMatch("/fufu-socializing/v3/api-docs")
                        //organize-bureau服务
                        .notMatch("/fufu-organize-bureau/doc.html")
                        .notMatch("/fufu-organize-bureau/swagger-ui/**")
                        .notMatch("/fufu-organize-bureau/webjars/**")
                        .notMatch("/fufu-organize-bureau/v3/api-docs/**")
                        .notMatch("/fufu-organize-bureau/v3/api-docs")
                        //organize-bureau服务
                        .notMatch("/fufu-search/doc.html")
                        .notMatch("/fufu-search/swagger-ui/**")
                        .notMatch("/fufu-search/webjars/**")
                        .notMatch("/fufu-search/v3/api-docs/**")
                        .notMatch("/fufu-search/v3/api-docs")
                        .check(r->StpUtil.checkLogin());
//                 权限认证 -- 不同模块, 校验不同权限
                SaRouter.match("/fufu-user/**")
                        .notMatch("/fufu-user/v1/login auth")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //user服务
                        .notMatch("/fufu-user/doc.html")
                        .notMatch("/fufu-user/swagger-ui/**")
                        .notMatch("/fufu-user/webjars/**")
                        .notMatch("/fufu-user/v3/api-docs/**")
                        .notMatch("/fufu-user/v3/api-docs")
                        .check(r -> StpUtil.checkPermission("user"));
                SaRouter.match("/fufu-socializing/**")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //socializing服务
                        .notMatch("/fufu-socializing/doc.html")
                        .notMatch("/fufu-socializing/swagger-ui/**")
                        .notMatch("/fufu-socializing/webjars/**")
                        .notMatch("/fufu-socializing/v3/api-docs/**")
                        .notMatch("/fufu-socializing/v3/api-docs")
                        .check(r -> StpUtil.checkPermission("order"));
                SaRouter.match("/fufu-organize-bureau/**")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //organize-bureau服务
                        .notMatch("/fufu-organize-bureau/doc.html")
                        .notMatch("/fufu-organize-bureau/swagger-ui/**")
                        .notMatch("/fufu-organize-bureau/webjars/**")
                        .notMatch("/fufu-organize-bureau/v3/api-docs/**")
                        .notMatch("/fufu-organize-bureau/v3/api-docs")
                        .check(r -> StpUtil.checkPermission("order"));
                SaRouter.match("/fufu-search/**")
                        //网关服务
                        .notMatch("/doc.html",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs")
                        //search服务
                        .notMatch("/fufu-search/doc.html")
                        .notMatch("/fufu-search/swagger-ui/**")
                        .notMatch("/fufu-search/webjars/**")
                        .notMatch("/fufu-search/v3/api-docs/**")
                        .notMatch("/fufu-search/v3/api-docs")
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
