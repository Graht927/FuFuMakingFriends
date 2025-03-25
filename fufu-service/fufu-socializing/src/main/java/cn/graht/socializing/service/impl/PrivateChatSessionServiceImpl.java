package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.pojos.PrivateChatSession;
import cn.graht.model.socializing.vos.SessionVo;
import cn.graht.model.socializing.vos.TempPrivateSessionVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.service.PrivateChatSessionService;
import cn.graht.socializing.service.caffeine.CaffeineCacheService;
import cn.graht.socializing.utils.UserRedissonCache;
import cn.graht.socializing.utils.UserToolUtils;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import cn.graht.socializing.mapper.PrivateChatSessionMapper;

import java.util.List;

/**
* @author Graht
* @description 针对表【private_chat_session(私聊会话表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class PrivateChatSessionServiceImpl extends ServiceImpl<PrivateChatSessionMapper, PrivateChatSession>
    implements PrivateChatSessionService {
    @Resource
    private PrivateChatSessionMapper privateChatSessionMapper;
    @Resource
    private UserToolUtils userToolUtils;

    @Override
    public List<SessionVo> selectListByUserId(String userId) {
        List<TempPrivateSessionVo> tempList  =  privateChatSessionMapper.selectListByUserId(userId);
        return tempList.stream().map(temp -> {
            SessionVo sessionVo = new SessionVo();
            sessionVo.setId(temp.getId());
            sessionVo.setLastMessageContent(temp.getLastMessageContent());
            sessionVo.setLastMessageTime(temp.getLastMessageTime());
            UserVo userCache = userToolUtils.getUserFromCacheOrFeign(temp.getUid());
            if (ObjectUtils.isNotEmpty(userCache)) {
                sessionVo.setName(userCache.getNickname());
                sessionVo.setAvatarUrl(userCache.getAvatarUrl());
            }
            return sessionVo;
        }).toList();
    }

}




