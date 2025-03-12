package cn.graht.socializing.mq.producer;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.producer.ProducerApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.socializing.service.DynamicNoticeService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
/**
 * @author GRAHT
 */
@Component
@ConditionalOnBean(DynamicNoticeService.class)
public class DynamicNoticeProducer {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private ProducerApi producerApi;
    @Value("${fufu.reqHeaderCode}")
    private String reqHeaderCode;
    @Value("${spring.application.name}")
    private String fromServiceName;
    public void sendDynamicNoticeMSG(String userId) {
        SendMSGRequestParams params = SendMSGRequestParams.builder()
                .from(fromServiceName)
                .content(SendMSGRequestParams.MSGContentParams.builder().uid(userId).build())
                .topic(ProducerTopics.USER_UNREGISTER_TOPIC)
                .timeout(3000).delayLevel(1).build();
        HttpHeaders headers = new HttpHeaders();
        String reqCode = DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes());
        ThrowUtils.throwIf(StringUtils.isBlank(reqCode), ErrorCode.PARAMS_ERROR);
        headers.add("reqCode", reqCode);
        producerApi.sendMsg(params, headers);
    }

}
   