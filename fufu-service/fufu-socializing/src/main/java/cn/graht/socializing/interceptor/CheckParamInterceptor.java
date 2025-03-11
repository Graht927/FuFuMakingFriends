package cn.graht.socializing.interceptor;

import cn.graht.socializing.utils.FsUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author GRAHT
 */
@Component
@Aspect
@Slf4j
public class CheckParamInterceptor {
    /**
     * 执行拦截
     */
    @Around("execution(* cn.graht.socializing.controller.v1.chat.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        FsUtils.checkParam(args);
        Object result = point.proceed();
        return result;
    }

}
