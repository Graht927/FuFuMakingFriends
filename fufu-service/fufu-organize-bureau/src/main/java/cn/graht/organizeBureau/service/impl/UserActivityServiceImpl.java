package cn.graht.organizeBureau.service.impl;


import cn.graht.common.commons.ErrorCode;
import cn.graht.common.exception.BusinessException;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.pojos.UserActivity;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.mapper.UserActivityMapper;
import cn.graht.organizeBureau.service.UserActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author graht
* @description 针对表【user_activity(用户队伍关系)】的数据库操作Service实现
*/
@Service
public class UserActivityServiceImpl extends ServiceImpl<UserActivityMapper, UserActivity>
    implements UserActivityService {

    @Resource
    private UserActivityMapper userTeamMapper;
    @Resource
    private UserFeignApi userFeignApi;

    @Override
    public List<UserVo> findUserTeamByTeamId(long teamId) {
        if (teamId < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<UserActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserActivity::getTeamId, teamId);
        queryWrapper.select(UserActivity::getUserId);
        List<UserActivity> userActivities = userTeamMapper.selectList(queryWrapper);
        List<UserVo> users = userActivities.stream().map(userActivity -> {
            String userId = userActivity.getUserId();
            UserVo userVo = userFeignApi.getUserInfo(userId).getData();
            return userVo;
        }).toList();
        if (Objects.isNull(users) || users.isEmpty()) throw new BusinessException(ErrorCode.NULL_ERROR);
        return users;
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




