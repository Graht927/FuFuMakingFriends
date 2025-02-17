package cn.graht.sms.controller.v1;

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
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Slf4j
@Tag(name = "短信发送",description = "短信Controller")
public class SMSController {
    @Resource
    private SMSParams smsParams;
    @Resource
    private SMSTemplateCode smsTemplateCode;
    @Resource
    private Redisson redisson;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @PostMapping("/g")
    @Operation(summary = "发送短信")
    @ApiResponse(responseCode = "200",description = "发送成功")
    @ApiResponse(responseCode = "10001",description = "sms参数错误")
    @ApiResponse(responseCode = "50000",description = "系统内部错误")
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
            String templateParams = getTemplateParams(smsRequestParam, captcha);
            SendSmsResponse sendSmsResponse = AliYunSmsUtils.sendSms(smsParams, smsTemplateCode.getTemplateCode(smsRequestParam.getTemplateCodeStr()), smsRequestParam.getPhone(), templateParams);
            //todo 最后删除
            if (!("remoteLogin".equals(smsRequestParam.getTemplateCodeStr())) && !ObjectUtils.isEmpty(sendSmsResponse)) {
                String redisKey = RedisKeyConstants.SMS_TEMPLATE_CODE_PREFIX + smsRequestParam.getTemplateCodeStr() + ":" + smsRequestParam.getPhone();
                String redisValue = String.valueOf(captcha);
                log.info("redis添加信息: [key:{},value: {}]",redisKey,redisValue);
                stringRedisTemplate.opsForValue().set(
                        redisKey,
                        redisValue,
                        RedisKeyConstants.SMS_TIMEOUT,
                        TimeUnit.SECONDS);
            }
            if ( !("remoteLogin".equals(smsRequestParam.getTemplateCodeStr())) && !ObjectUtils.isEmpty(sendSmsResponse) && "OK".equals(sendSmsResponse.getBody().getMessage())) {
                String redisKey = RedisKeyConstants.SMS_TEMPLATE_CODE_PREFIX + smsRequestParam.getTemplateCodeStr() + ":" + smsRequestParam.getPhone();
                String redisValue = String.valueOf(captcha);
                log.info("redis添加信息: [key:{},value: {}]",redisKey,redisValue);
                stringRedisTemplate.opsForValue().set(
                        redisKey,
                        redisValue,
                        RedisKeyConstants.SMS_TIMEOUT,
                        TimeUnit.SECONDS);
                return ResultUtil.ok();
            }
        } finally {
            lock.unlock();
        }
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }

    private static @NotNull String getTemplateParams(SMSRequestParam smsRequestParam, int captcha) {
        String templateParams = "";
        if ("remoteLogin".equals(smsRequestParam.getTemplateCodeStr())){
            templateParams = "{\"user_nick\":\""+ smsRequestParam.getUserNick()+"\",\"time\":\""+ smsRequestParam.getTime()+"\",\"address\":\""+ smsRequestParam.getAddress()+"\"}";
        }
        if ("login".equals(smsRequestParam.getTemplateCodeStr())){
            templateParams = "{\"code\":"+"\""+ captcha +"\"}";
        }
        if ("register".equals(smsRequestParam.getTemplateCodeStr())){
            templateParams = "{\"code\":"+"\""+ captcha +"\"}";
        }
        return templateParams;
    }
}
