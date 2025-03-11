package cn.graht.socializing.service.impl;

import cn.graht.model.socializing.pojos.GroupChatSession;
import cn.graht.socializing.service.GroupChatSessionService;
import cn.graht.socializing.mapper.GroupChatSessionMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author Graht
* @description 针对表【group_chat_session(私聊会话表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class GroupChatSessionServiceImpl extends ServiceImpl<GroupChatSessionMapper, GroupChatSession>
    implements GroupChatSessionService {

}




