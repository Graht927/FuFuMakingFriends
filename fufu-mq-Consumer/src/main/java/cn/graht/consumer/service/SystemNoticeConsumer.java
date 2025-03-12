package cn.graht.consumer.service;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.socializing.NoticeFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GRAHT
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = ProducerTopics.SYSTEM_NOTICE_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_SYSTEM_NOTICE)
public class SystemNoticeConsumer implements RocketMQListener<String> {

    @Resource
    private NoticeFeignApi noticeFeignApi;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        String content1 = content.getContent();
        Map<String,String> info = JSONUtil.toBean(content1, Map.class);
        String type = info.get("type");
        //发送给谁
        if ("focus".equals(type)){
            SystemNoticeDto noticeDto = new SystemNoticeDto();
            noticeDto.setUserId(content.getUid());
            noticeDto.setType(info.get("type"));
            noticeDto.setUserId2(info.get("focusUserId"));
            HashMap<String,String> map = new HashMap<>();
            map.put("focusUserName",info.get("focusUserName"));
            map.put("focusUserId",info.get("focusUserId"));
            map.put("focusUserAvatar",info.get("focusUserAvatar"));
            map.put("message","关注了你");
            noticeDto.setContent(JSONUtil.toJsonStr(map));
            ResultApi<Boolean> booleanResultApi = noticeFeignApi.addNotice(noticeDto);
            ThrowUtils.throwIf(!booleanResultApi.getData(), ErrorCode.SYSTEM_ERROR);
            log.info("发送系统通知成功: 用户: {} 关注了 用户: {} ",content.getUid(),info.get("focusUserId"));
        }

    }

}
   