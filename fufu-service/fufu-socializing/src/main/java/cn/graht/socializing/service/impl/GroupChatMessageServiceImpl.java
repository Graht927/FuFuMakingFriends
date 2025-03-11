package cn.graht.socializing.service.impl;

import cn.graht.model.socializing.pojos.GroupChatMessage;
import cn.graht.socializing.service.GroupChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.graht.socializing.mapper.GroupChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author Graht
* @description 针对表【group_chat_message(群聊消息表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class GroupChatMessageServiceImpl extends ServiceImpl<GroupChatMessageMapper, GroupChatMessage>
    implements GroupChatMessageService {

}




