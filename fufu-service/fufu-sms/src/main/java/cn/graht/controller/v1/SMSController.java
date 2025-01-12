package cn.graht.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.UserConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.sms.dto.SMSRequestParam;
import cn.graht.utils.aliSendSMS.AliYunSmsUtils;
import cn.graht.utils.aliSendSMS.SMSParams;
import cn.graht.utils.aliSendSMS.SMSTemplateCode;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
public class SMSController {
    private static final Logger log = LoggerFactory.getLogger(SMSController.class);
    @Resource
    private SMSParams smsParams;
    @Resource
    private SMSTemplateCode smsTemplateCode;

    @Resource
    private Redisson redisson;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/g")
    public ResultApi requestSms(@RequestBody SMSRequestParam smsRequestParam) {
        RLock lock = redisson.getLock(RedisKeyConstants.SMS_LOCK_PREFIX + smsRequestParam.getPhone());
        lock.lock();
        try {
            ThrowUtils.throwIf(ObjectUtils.isEmpty(smsRequestParam)
                            || StringUtils.isBlank(smsRequestParam.getPhone())
                            || StringUtils.isBlank(smsRequestParam.getTemplateCodeStr())
                            || !ReUtil.isMatch(UserConstant.PHONE_NUMBER_PATTERN, smsRequestParam.getPhone())
                    , ErrorCode.SMS_PARAMS_ERROR);
            int captcha = smsParams.getCaptcha();
            SendSmsResponse sendSmsResponse = AliYunSmsUtils.sendSms(smsParams, smsTemplateCode.getTemplateCode(smsRequestParam.getTemplateCodeStr()), smsRequestParam.getPhone(), captcha);
            log.info("{}", JSONUtil.toJsonStr(sendSmsResponse));
            if (!ObjectUtils.isEmpty(sendSmsResponse)) {
                stringRedisTemplate.opsForValue().set(RedisKeyConstants.SMS_TEMPLATE_CODE_PREFIX + smsRequestParam.getPhone(),
                        String.valueOf(captcha),
                        RedisKeyConstants.SMS_TIMEOUT,
                        TimeUnit.SECONDS);
                return ResultUtil.ok(sendSmsResponse);
            }
        } finally {
            lock.unlock();
        }
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }
}
