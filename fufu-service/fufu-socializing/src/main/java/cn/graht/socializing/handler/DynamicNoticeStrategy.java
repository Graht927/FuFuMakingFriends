package cn.graht.socializing.handler;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.constant.ProducerTopics;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.enums.MessageDelayLevelEnum;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.producer.ProducerApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.mq.dto.producer.SendMSGRequestParams;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.DynamicVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.common.enums.NoticeType;
import cn.graht.model.socializing.pojos.notice.DynamicNoticeThumbsUpContent;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import cn.graht.socializing.utils.UserRedissonCache;
import cn.graht.socializing.utils.UserToolUtils;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Map;

/**
 * @author GRAHT
 */
@Component("dynamic:notice:")
public class DynamicNoticeStrategy implements FuFuEventStrategy {
    @Resource
    private CaffeineCacheService caffeineCacheService;
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private ProducerApi producerApi;
    @Value("${fufu.reqHeaderCode}")
    private String reqHeaderCode;
    @Value("${spring.application.name}")
    private String serviceName;
    @Resource
    private UserRedissonCache userRedissonCache;
    @Resource
    private UserToolUtils userToolUtils;
    @Override
    public void handle(Map<String, String> param) {
        //处理逻辑
        String type = param.get("type");
        if (NoticeType.THUMBS_UP.getValue().equals(type)){

            //谁点赞了谁的动态
            String userId2 = param.get("userId2");
            String dynamicId = param.get("dynamicId");
            UserVo userVo = userToolUtils.getUserFromCacheOrFeign(userId2);
            DynamicNoticeDto dynamicNoticeDto = new DynamicNoticeDto();
            ResultApi<DynamicVo> dynamicById = userFeignApi.getDynamicById(Long.parseLong(dynamicId));
            ThrowUtils.throwIf(ObjectUtils.isEmpty(dynamicById)
                    || ObjectUtils.isEmpty(dynamicById.getData())
                    || dynamicById.getCode() != ErrorCode.SUCCESS.getCode(), ErrorCode.PARAMS_ERROR);
            //发送到哪里
            String userId = dynamicById.getData().getUserId();
            dynamicNoticeDto.setUserId(userId);
            DynamicNoticeThumbsUpContent dynamicNoticeThumbsUpContent = new DynamicNoticeThumbsUpContent();
            dynamicNoticeThumbsUpContent.setDynamicId(dynamicId);
            dynamicNoticeThumbsUpContent.setUserId2Name(userVo.getNickname());
            dynamicNoticeThumbsUpContent.setUserId2Avatar(userVo.getAvatarUrl());
            dynamicNoticeThumbsUpContent.setUserId2(userId2);
            dynamicNoticeThumbsUpContent.setCoverImages(dynamicById.getData().getImage());
            dynamicNoticeThumbsUpContent.setType(NoticeType.THUMBS_UP.getValue());
            dynamicNoticeDto.setType(NoticeType.THUMBS_UP.getValue());
            dynamicNoticeDto.setContent(JSONUtil.toJsonStr(dynamicNoticeThumbsUpContent));
            SendMSGRequestParams.MSGContentParams content = SendMSGRequestParams.MSGContentParams.builder()
                    .uid(userId)
                    .nickname(userVo.getNickname())
                    .phone(userVo.getPhone())
                    .content(JSONUtil.toJsonStr(dynamicNoticeThumbsUpContent))
                    .build();
            SendMSGRequestParams sendMSGRequestParams = SendMSGRequestParams.builder()
                    .from(serviceName)
                    .topic(ProducerTopics.DYNAMIC_NOTICE_TOPIC)
                    .content(content)
                    .delayLevel(MessageDelayLevelEnum.L1.getLevel())
                    .timeout(1000)
                    .build();
            ThrowUtils.throwIf(ObjectUtils.isEmpty(sendMSGRequestParams), ErrorCode.PARAMS_ERROR);
            HttpHeaders headers = new HttpHeaders();
            String reqCode = DigestUtils.md5DigestAsHex((SystemConstant.SALT + reqHeaderCode).getBytes());
            ThrowUtils.throwIf(StringUtils.isBlank(reqCode), ErrorCode.PARAMS_ERROR);
            headers.add("reqCode", reqCode);
            producerApi.sendMsgAsync(sendMSGRequestParams, headers);
        }
    }
}
