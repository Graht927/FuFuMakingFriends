package cn.graht.organizeBureau.service;

import cn.graht.model.organizeBureau.pojos.UserTeam;
import cn.graht.model.user.vos.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author graht
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service
*/
public interface UserTeamService extends IService<UserTeam> {
    List<UserVo> findUserTeamByTeamId(long timeId);

    /**
     *  判断用户之前是否加入过队伍
     * @param teamId
     * @param id
     * @return
     */
    UserTeam checkOldUser(long teamId, String userId);

    /**
     *  修改之前加入过队伍用户的id
     * @param userTeamId
     * @return
     */
    boolean modifyIsDel(long userTeamId);
}
