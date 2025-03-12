package cn.graht.consumer.service;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.sms.TXFeignApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.sms.dto.SMSRequestParam;
import cn.graht.model.user.vos.UserVo;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
/**
 * @author GRAHT
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = ProducerTopics.USER_UNREGISTER_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_UNREGISTRY_USER)
public class UserUnregisterConsumer implements RocketMQListener<String> {

    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private TXFeignApi txFeignApi;
    @Value("${ali.sms.reqHeaderCode}")
    private String reqHeaderCode;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        String uid = content.getUid();
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(uid);
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        ResultApi<Boolean> remove = userFeignApi.UnregisterRemoveById(uid);
        Boolean data = remove.getData();
        if (!data) log.info(" 注销失败 有可能因为已经取消注销: {} ", uid);
        else {
            SMSRequestParam smsRequestParam = new SMSRequestParam();
            smsRequestParam.setTemplateCodeStr("unregister");
            smsRequestParam.setPhone(userInfo.getData().getPhone());
            HttpHeaders headers =  new HttpHeaders();
            headers.add("reqCode", DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes()));
            txFeignApi.requestSms(smsRequestParam, headers);
            log.info("注销成功: {} ", uid);
        }
    }

}
   