package cn.graht.organizeBureau.service.impl;


import cn.graht.common.commons.ErrorCode;
import cn.graht.common.exception.BusinessException;
import cn.graht.model.organizeBureau.pojos.UserActivity;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.mapper.UserTeamMapper;
import cn.graht.organizeBureau.service.UserTeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author graht
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserActivity>
    implements UserTeamService {

    @Resource
    private UserTeamMapper userTeamMapper;

    @Override
    public List<UserVo> findUserTeamByTeamId(long teamId) {
        if (teamId < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        List<User> users  = userTeamMapper.findUserTeamByTeamId(teamId);
        if (Objects.isNull(users) || users.isEmpty()) throw new BusinessException(ErrorCode.NULL_ERROR);
        return users.stream().map(user -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);
            return userVo;
        }).collect(Collectors.toList());
    }

    @Override
    public UserActivity checkOldUser(long teamId, String userId) {
        UserActivity userTeam = userTeamMapper.checkOldUser(teamId,userId);
        return Objects.isNull(userTeam) ? null : userTeam;
    }

    @Override
    public boolean modifyIsDel(long userTeamId) {
        int i =  userTeamMapper.modifyIsDel(userTeamId);
        return i == 1;
    }
}




