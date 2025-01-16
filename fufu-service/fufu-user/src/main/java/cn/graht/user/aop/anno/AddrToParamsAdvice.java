package cn.graht.user.aop.anno;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.sms.TXFeignApi;
import cn.graht.model.sms.dto.TenXunRequestParams;
import cn.graht.model.sms.pojos.LocationData;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.utils.tenXunMapApi.TenXunApiConstant;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author GRAHT
 */
@Aspect
@Component
@Slf4j
public class AddrToParamsAdvice {
    @Pointcut("execution(public * cn.graht.user.controller.*Controller.*(..))")
    public void ipPoint() {
    }

    @Pointcut("")
    public void ipToParam() {
    }

    @Resource
    private TXFeignApi txFeignApi;
    @Value("${tx.reqHeaderCode}")
    private String reqCode;

    @Before("@annotation(cn.graht.user.aop.anno.AddrToParam)")
    public void doInterceptor(JoinPoint joinPoint) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String location = request.getHeader("location");
        ThrowUtils.throwIf(StringUtils.isBlank(location), ErrorCode.FORBIDDEN_ERROR);
        TenXunRequestParams tenXunRequestParams = new TenXunRequestParams();
        tenXunRequestParams.setU(TenXunApiConstant.TX_REVERSE_ADDRESS_RESOLUTION.getValue());
        HashMap<String, String> params = new HashMap<>();
        params.put("location", location);
        tenXunRequestParams.setParams(params);
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("reqCode", DigestUtils.md5DigestAsHex((SystemConstant.SALT+reqCode).getBytes()));
        ResultApi<Object> tx = txFeignApi.tx(tenXunRequestParams, headers);
        String result = JSONUtil.toJsonStr(((LinkedHashMap<String, Object>) tx.getData()).get("result"));
        LocationData data = (LocationData) JSONUtil.toBean(result, LocationData.class);
        LocationData.AdInfo adInfo = data.getAd_info();
        String city = adInfo.getCity();
        String province = adInfo.getProvince();
        String addr = province + "-" + city;
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof LoginDto) {
                ((LoginDto) arg).setAddr(addr);
            } else if (arg instanceof RegisterDto) {
                ((RegisterDto) arg).setAddr(addr);
            }
        }
    }
}
