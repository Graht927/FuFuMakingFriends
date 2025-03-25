package cn.graht.socializing.mapper;

import cn.graht.model.socializing.pojos.GroupChatMember;
import cn.graht.model.socializing.vos.SessionVo;
import cn.graht.model.socializing.vos.TempGroupSessionVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Graht
* @description 针对表【group_chat_member(群聊成员表)】的数据库操作Mapper
* @createDate 2025-03-11 09:55:40
*/
public interface GroupChatMemberMapper extends BaseMapper<GroupChatMember> {

    List<TempGroupSessionVo> selectListByUid(String userId);

}




