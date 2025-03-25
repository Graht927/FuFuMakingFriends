package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ResultApi;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.pojos.GroupChatMember;
import cn.graht.model.socializing.vos.SessionVo;
import cn.graht.model.socializing.vos.TempGroupSessionVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.GroupChatMemberMapper;
import cn.graht.socializing.service.GroupChatMemberService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Graht
* @description 针对表【group_chat_member(群聊成员表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class GroupChatMemberServiceImpl extends ServiceImpl<GroupChatMemberMapper, GroupChatMember>
    implements GroupChatMemberService {

    @Resource
    private GroupChatMemberMapper groupChatMemberMapper;
    @Resource
    private UserFeignApi userFeignApi;
    @Override
    public List<SessionVo> selectListByUserId(String userId) {
        List<TempGroupSessionVo> tempGroupSessionVos = groupChatMemberMapper.selectListByUid(userId);
        return tempGroupSessionVos.stream().map(temp -> {
            SessionVo sessionVo = new SessionVo();
            BeanUtils.copyProperties(temp, sessionVo);
            if (ObjectUtil.isNotEmpty(temp.getSenderId()) || StringUtils.isNotBlank(temp.getSenderId())){
                ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(temp.getSenderId());
                if (ObjectUtil.isNotEmpty(userInfo)) {
                    sessionVo.setLastMessageContent(userInfo.getData().getNickname() + ":" + temp.getLastMessageContent());
                }
            }
            return sessionVo;
        }).toList();
    }
}




