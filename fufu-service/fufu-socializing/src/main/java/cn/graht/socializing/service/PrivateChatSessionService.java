package cn.graht.socializing.service;

import cn.graht.model.socializing.pojos.PrivateChatSession;
import cn.graht.model.socializing.vos.SessionVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Graht
* @description 针对表【private_chat_session(私聊会话表)】的数据库操作Service
* @createDate 2025-03-11 09:55:40
*/
public interface PrivateChatSessionService extends IService<PrivateChatSession> {

    List<SessionVo> selectListByUserId(String userId);
}
