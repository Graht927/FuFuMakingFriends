package cn.graht.sms.config.interceptor;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.sms.utils.SpringContextUtils;
import cn.graht.utils.aliSendSMS.SMSParams;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author GRAHT
 */
@Slf4j
public class SMSInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断req路径
        if ("/v1/g".equals(request.getRequestURI())) {
            String reqCode = request.getHeader("reqCode");
            SMSParams smsParams = SpringContextUtils.getBean(SMSParams.class);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(smsParams), ErrorCode.FORBIDDEN_ERROR);
            String reqHeaderCode = smsParams.getReqHeaderCode();
            ThrowUtils.throwIf(StringUtils.isBlank(reqCode), ErrorCode.FORBIDDEN_ERROR);
            ThrowUtils.throwIf(!DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes()).equals(reqCode), ErrorCode.FORBIDDEN_ERROR);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
