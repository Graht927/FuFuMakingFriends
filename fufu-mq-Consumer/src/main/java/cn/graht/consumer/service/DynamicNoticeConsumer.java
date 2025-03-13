package cn.graht.consumer.service;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.socializing.NoticeFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
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
@RocketMQMessageListener(topic = ProducerTopics.DYNAMIC_NOTICE_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_DYNAMIC_NOTICE)
public class DynamicNoticeConsumer implements RocketMQListener<String> {
    @Resource
    private NoticeFeignApi noticeFeignApi;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        String content1 = content.getContent();
        Map<String,String> info = JSONUtil.toBean(content1, Map.class);
        String type = info.get("type");
        //发送给谁
        if ("thumbsUp".equals(type)){
            DynamicNoticeDto noticeDto = new DynamicNoticeDto();
            noticeDto.setUserId(content.getUid());
            noticeDto.setType(info.get("type"));
            noticeDto.setUserId2(info.get("userId2"));
            noticeDto.setDynamicId(Integer.parseInt(info.get("dynamicId")));
            noticeDto.setCoverImages(info.get("coverImages"));
            HashMap<String,String> map = new HashMap<>();
            map.put("coverImages",info.get("coverImages"));
            map.put("userId2Name",info.get("userId2Name"));
            map.put("userId2Avatar",info.get("userId2Avatar"));
            map.put("message","点赞了你的动态");
            noticeDto.setContent(JSONUtil.toJsonStr(map));
            ResultApi<Boolean> booleanResultApi = noticeFeignApi.addNotice(noticeDto);
            ThrowUtils.throwIf(!booleanResultApi.getData(), ErrorCode.SYSTEM_ERROR);
            log.info("发送动态通知成功: 用户: {} 点赞了 用户: {} 的{}条动态信息 ",info.get("userId2"),content.getUid(),info.get("dynamicId"));
        }
    }

}
   