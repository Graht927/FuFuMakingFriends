package cn.graht.socializing.service;

import cn.graht.model.socializing.pojos.GroupChatMember;
import cn.graht.model.socializing.vos.SessionVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Graht
* @description 针对表【group_chat_member(群聊成员表)】的数据库操作Service
* @createDate 2025-03-11 09:55:40
*/
public interface GroupChatMemberService extends IService<GroupChatMember> {


    List<SessionVo> selectListByUserId(String userId);

}
