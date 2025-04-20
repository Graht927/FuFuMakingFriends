package cn.graht.organizeBureau.event;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.enums.MessageDelayLevelEnum;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.producer.ProducerApi;
import cn.graht.feignApi.socializing.SocializingFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.socializing.pojos.GroupChatSession;
import cn.graht.organizeBureau.controller.v1.ActivityController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Map;
/**
 * @author GRAHT
 */
@Component
@Slf4j
public class FuFuEventListener implements ApplicationListener<FuFuEvent> {

    @Resource
    private ProducerApi producerApi;
    @Value("${fufu.reqHeaderCode}")
    private String reqHeaderCode;
    @Value("${spring.application.name}")
    private String fromServiceName;
    @Resource
    private SocializingFeignApi socializingFeignApi;
    @Override
    public void onApplicationEvent(FuFuEvent event) {
        String msgType = event.getMessage();
        Map<String, String> params = event.getParams();
        if (msgType.equals("create_group_session")){
            String activityId = params.get("activityId");
            String userId = params.get("userId");
            SendMSGRequestParams content = SendMSGRequestParams.builder()
                    .from(fromServiceName)
                    .content(SendMSGRequestParams.MSGContentParams.builder().uid(userId).content(activityId).build())
                    .topic(ProducerTopics.GROUP_SESSION_CREATE_TOPIC)
                    .timeout(3000).delayLevel(MessageDelayLevelEnum.L1.getLevel()).build();
            HttpHeaders headers = new HttpHeaders();
            String reqCode = DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes());
            ThrowUtils.throwIf(StringUtils.isBlank(reqCode), ErrorCode.PARAMS_ERROR);
            headers.add("reqCode", reqCode);
            ResultApi<Object> objectResultApi = producerApi.sendMsg(content, headers);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(objectResultApi), ErrorCode.SYSTEM_ERROR);
            log.info("send create_group_session_msg success!");
        }
        if (msgType.equals("send_join_group_session")){
            String activityId = params.get("activityId");
            ResultApi<GroupChatSession> sessionByActivityId = socializingFeignApi.getSessionByActivityId(Integer.parseInt(activityId));
            ThrowUtils.throwIf(ObjectUtils.isEmpty(sessionByActivityId), ErrorCode.SYSTEM_ERROR);
            String sessionId = sessionByActivityId.getData().getId().toString();
            String userId = params.get("userId");
            SendMSGRequestParams content = SendMSGRequestParams.builder()
                    .from(fromServiceName)
                    .content(SendMSGRequestParams.MSGContentParams.builder().uid(userId).content(sessionId).build())
                    .topic(ProducerTopics.GROUP_SESSION_JOIN_TOPIC)
                    .timeout(3000).delayLevel(MessageDelayLevelEnum.L1.getLevel()).build();
            HttpHeaders headers = new HttpHeaders();
            String reqCode = DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes());
            ThrowUtils.throwIf(StringUtils.isBlank(reqCode), ErrorCode.PARAMS_ERROR);
            headers.add("reqCode", reqCode);
            ResultApi<Object> objectResultApi = producerApi.sendMsg(content, headers);
            ThrowUtils.throwIf(ObjectUtils.isEmpty(objectResultApi), ErrorCode.SYSTEM_ERROR);
            log.info("send create_group_session_msg success!");
        }
    }






}

