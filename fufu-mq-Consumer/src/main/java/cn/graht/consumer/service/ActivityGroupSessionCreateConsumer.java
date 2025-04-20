package cn.graht.consumer.service;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.GroupConsumers;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.organizeBureau.ActivityFeignApi;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.organizeBureau.dtos.GetDto;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.socializing.dtos.CreateGroupSessionDto;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author GRAHT
 */
@Service
@Slf4j
@RocketMQMessageListener(topic = ProducerTopics.GROUP_SESSION_CREATE_TOPIC, consumerGroup = GroupConsumers.GROUP_CONSUMER_ACTIVITY_GROUP_SESSION_CREATE)
public class ActivityGroupSessionCreateConsumer implements RocketMQListener<String> {
    @Resource
    private SocializingFeignApi socializingFeignApi;
    @Resource
    private ActivityFeignApi activityFeignApi;

    @Override
    public void onMessage(String message) {
        SendMSGRequestParams.MSGContentParams content = JSONUtil.toBean(message, SendMSGRequestParams.MSGContentParams.class);
        String activityId = content.getContent();
        String userId = content.getUid();
        GetDto getDto = new GetDto();
        getDto.setUid(userId);
        getDto.setTeamId(Long.parseLong(activityId));
        ResultApi<ActivityUserVo> teamInfoByTid = activityFeignApi.getTeamInfo(getDto);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(teamInfoByTid), ErrorCode.PARAMS_ERROR);
        ActivityUserVo data = teamInfoByTid.getData();
        CreateGroupSessionDto createGroupSessionDto = new CreateGroupSessionDto();
        createGroupSessionDto.setName(data.getName());
        createGroupSessionDto.setCreatorId(userId);
        createGroupSessionDto.setAvatarUrl(data.getTeamImage());
        createGroupSessionDto.setActivityId(activityId);
        createGroupSessionDto.setUserId(userId);
        ResultApi<Integer> session = socializingFeignApi.createSession(createGroupSessionDto);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(session), ErrorCode.PARAMS_ERROR);
        log.info("createSession success id:{}", session.getData());
    }
}
