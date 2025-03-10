package cn.graht.organizeBureau.mapper;


import cn.graht.model.organizeBureau.pojos.UserActivity;
import cn.graht.model.user.pojos.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author graht
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
*/
public interface UserTeamMapper extends BaseMapper<UserActivity> {
    /**
     * 通过关联表获取队友
     * @param teamId
     * @return
     */
    List<User> findUserTeamByTeamId(long teamId);

    /**
     *  验证之前用户是否加入过该队伍
     * @param teamId
     * @param userId
     * @return
     */
    UserActivity checkOldUser(long teamId, String userId);

    /**
     *  修改之前用户的isDel标签状态
     * @param userTeamId
     * @return
     */
    int modifyIsDel(long userTeamId);
}




