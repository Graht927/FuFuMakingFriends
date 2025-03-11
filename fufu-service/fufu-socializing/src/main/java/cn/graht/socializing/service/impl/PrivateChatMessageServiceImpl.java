package cn.graht.socializing.service.impl;

import cn.graht.model.socializing.pojos.PrivateChatMessage;
import cn.graht.socializing.service.PrivateChatMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.graht.socializing.mapper.PrivateChatMessageMapper;
/**
* @author Graht
* @description 针对表【private_chat_Message(消息表)】的数据库操作Service实现
* @createDate 2025-03-11 09:55:40
*/
@Service
public class PrivateChatMessageServiceImpl extends ServiceImpl<PrivateChatMessageMapper, PrivateChatMessage>
    implements PrivateChatMessageService {

}




