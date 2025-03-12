package cn.graht.consumer.service;

import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.feignApi.socializing.NoticeFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @author GRAHT
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = ProducerTopics.DYNAMIC_NOTICE_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_DYNAMIC_NOTICE)
public class DynamicNoticeConsumer implements RocketMQListener<String> {
    @Resource
    private NoticeFeignApi noticeFeignApi;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        log.info("content: {}",content);
    }

}
   