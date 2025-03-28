package cn.graht.feignApi.interceptor;//省略包名

import cn.hutool.core.util.ObjectUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Graht
 * @version v0.0.1
 * @apiNote feign传递token拦截统一处理
 */
public class TokenDelayRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取当前请求上下文
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNotEmpty(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            requestTemplate.header("Authorization", request.getHeader("Authorization"));
        }
    }
}