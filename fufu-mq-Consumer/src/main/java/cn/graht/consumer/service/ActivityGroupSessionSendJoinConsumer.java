package cn.graht.consumer.service;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.organizeBureau.ActivityFeignApi;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.socializing.dtos.CreateGroupChatMemberDto;
import cn.graht.model.socializing.dtos.CreateGroupSessionDto;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author GRAHT
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = ProducerTopics.GROUP_SESSION_JOIN_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_ACTIVITY_GROUP_SESSION_JOIN)
public class ActivityGroupSessionSendJoinConsumer implements RocketMQListener<String> {
    @Resource
    private SocializingFeignApi socializingFeignApi;
    @Resource
    private ActivityFeignApi activityFeignApi;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        String sessionId = content.getContent();
        String userId = content.getUid();
        CreateGroupChatMemberDto memberDto = new CreateGroupChatMemberDto();
        memberDto.setGroupId(Integer.parseInt(sessionId));
        memberDto.setUserId(userId);
        memberDto.setRole(2);
        memberDto.setJoinTime(new Date());
        ResultApi<Boolean> booleanResultApi = socializingFeignApi.addMember(memberDto);
        ThrowUtils.throwIf(!booleanResultApi.getData(), ErrorCode.SYSTEM_ERROR);
        log.info("add group chat success ! userId:{}",userId);
    }
}
