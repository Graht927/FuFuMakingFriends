package cn.graht.socializing.mapper;

import cn.graht.model.socializing.pojos.PrivateChatSession;
import cn.graht.model.socializing.vos.TempPrivateSessionVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Graht
* @description 针对表【private_chat_session(私聊会话表)】的数据库操作Mapper
* @createDate 2025-03-11 09:55:40
* @Entity generator.domain.PrivateChatSession
*/
public interface PrivateChatSessionMapper extends BaseMapper<PrivateChatSession> {

    List<TempPrivateSessionVo> selectListByUserId(String userId);
}




