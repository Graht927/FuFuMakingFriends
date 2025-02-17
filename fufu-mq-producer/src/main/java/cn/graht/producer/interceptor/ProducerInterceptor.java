package cn.graht.producer.interceptor;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author GRAHT
 */
@Slf4j
public class ProducerInterceptor implements HandlerInterceptor {
    @Value("${fufu.reqHeaderCode}")
    private String reqHeaderCode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断req路径
        if ("/v1/producer/sendMsg".equals(request.getRequestURI())) {
            String reqCode = request.getHeader("reqCode");
            ThrowUtils.throwIf(StringUtils.isBlank(reqHeaderCode), ErrorCode.FORBIDDEN_ERROR);
            ThrowUtils.throwIf(!DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes()).equals(reqCode), ErrorCode.FORBIDDEN_ERROR);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
