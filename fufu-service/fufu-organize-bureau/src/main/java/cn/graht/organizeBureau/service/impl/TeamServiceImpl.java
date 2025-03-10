package cn.graht.organizeBureau.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.constant.UserConstant;
import cn.graht.common.exception.BusinessException;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.dtos.TeamQuitRequest;
import cn.graht.model.organizeBureau.dtos.TeamUpdateRequest;
import cn.graht.model.organizeBureau.pojos.Activity;
import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.dtos.TeamSendJoinRequest;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.organizeBureau.pojos.UserActivity;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.mapper.TeamMapper;
import cn.graht.organizeBureau.service.TeamService;
import cn.graht.organizeBureau.service.UserTeamService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import jakarta.servlet.http.HttpServletRequest;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @author graht
 * @description 针对表【team(队伍)】的数据库操作Service实现
 */
@Service
public class TeamServiceImpl extends
        ServiceImpl<TeamMapper, Activity>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private Redisson redisson;

    @Override
    //todo 分布式事务
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Activity activity, User user) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(activity)
                        || StringUtils.isBlank(activity.getName()) || activity.getName().length() >= 20
                        || StringUtils.isBlank(activity.getDescription())
                        || ObjectUtils.isEmpty(activity.getMaxNum()) || activity.getMaxNum() < 0
                        || ObjectUtils.isEmpty(activity.getCurrentNum()) || activity.getCurrentNum() < 0
                        || ObjectUtils.isEmpty(activity.getExpireTime())
                        || StringUtils.isBlank(activity.getTeamImage())
                        || StringUtils.isBlank(activity.getAddress())
                , ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCode.NOT_LOGIN_ERROR);
        final String userId = user.getId();
        //校验信息
        int maxNum = Optional.ofNullable(activity.getMaxNum()).orElse(0);
        ThrowUtils.throwIf(maxNum < 1 || maxNum > 15, ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");

        Date expireTime = activity.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间 > 当前时间");
        }
        //todo 有bug 并发安全
        long count = count(new QueryWrapper<Activity>().eq("userId", userId));
        if (count >= 5)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前队伍数量已经是最大值，如需新建请查看vip");
        activity.setId(null);
        activity.setUserId(userId);
        boolean save = save(activity);
        ThrowUtils.throwIf(!save, ErrorCode.PARAMS_ERROR, "创建活动失败");
        Long teamId = activity.getId();
        UserActivity userTeam = new UserActivity();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean save1 = userTeamService.save(userTeam);
        ThrowUtils.throwIf(!save1, ErrorCode.PARAMS_ERROR, "创建活动失败");
        return activity.getId();
    }

    @Override
    public List<ActivityUserVo> listTeamByPage(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long tid = Optional.ofNullable(teamQuery.getId()).orElse(0l);
        long pageNum = teamQuery.getPageNum();
        long pageSize = teamQuery.getPageSize();
        String name = teamQuery.getName();
        String searchText = teamQuery.getSearchText();
        String description = teamQuery.getDescription();
        String address = teamQuery.getAddress();
        Date startTime = teamQuery.getStartTime();
        String userId = teamQuery.getUserId();
        if (pageNum < 0 || pageSize <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        //通过name来查询
        //分页
        IPage<Activity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<Activity>();
        if (!Objects.isNull(teamQuery.getExpireTime())) {
            queryWrapper.gt(Activity::getExpireTime, DateUtil.now());
        }
        //判断条件
        if (!StringUtils.isBlank(name)) {
            queryWrapper.like(Activity::getName, name);
        }
        if (!ObjectUtils.isNotEmpty(startTime)) {
            queryWrapper.eq(Activity::getStartTime, startTime);
        }

        if (!StringUtils.isBlank(address)) {
            queryWrapper.like(Activity::getName, name);
        }
        if (!StringUtils.isBlank(searchText)) {
            queryWrapper.like(Activity::getName, searchText).or().like(Activity::getDescription, searchText);
        }
        if (tid > 0) {
            queryWrapper.eq(Activity::getId, tid);
        }
        if (!StringUtils.isBlank(description)) {
            queryWrapper.like(Activity::getDescription, description);
        }
        if (userId != null) {
            queryWrapper.eq(Activity::getUserId, userId);
        }
        User loginUser = (User) StpUtil.getLoginId();
        IPage<Activity> page1 = teamMapper.selectPage(page, queryWrapper);
        List<Activity> records = page1.getRecords();
        return records.stream().filter(team -> {
            if (team.getExpireTime().before(new Date())) {
                return false;
            }
            if (!Objects.isNull(loginUser)) {
                if (team.getUserId().equals(loginUser.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList()).stream().map(team -> {
            ActivityUserVo teamUserVo = new ActivityUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            Long id = team.getId();
            ThrowUtils.throwIf(ObjectUtils.isEmpty(id) || id < 0, ErrorCode.PARAMS_ERROR);

            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(id);
            ThrowUtils.throwIf(CollectionUtils.isEmpty(userTeamByTeamId), ErrorCode.PARAMS_ERROR);
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            String teamImage = team.getTeamImage();
            ThrowUtils.throwIf(StringUtils.isBlank(teamImage), ErrorCode.PARAMS_ERROR);
            teamUserVo.setTeamImage(Arrays.asList(teamImage.split(",")));
            return teamUserVo;
        }).collect(Collectors.toList()).stream().filter(teamUserVo -> {
            if (!Objects.isNull(loginUser)) {
                for (UserVo teamUserInfo : teamUserVo.getTeamUserInfos()) {
                    if (teamUserInfo.getId().equals(loginUser.getId())) {
                        return false;
                    }
                }
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        ThrowUtils.throwIf(
                Objects.isNull(teamUpdateRequest)
                        || Objects.isNull(loginUser)
                , ErrorCode.PARAMS_ERROR);
        Long id = teamUpdateRequest.getId();
        if (StringUtils.isBlank(teamUpdateRequest.getName())) {
            teamUpdateRequest.setName(null);
        }
        if (StringUtils.isBlank(teamUpdateRequest.getDescription())) {
            teamUpdateRequest.setDescription(null);
        }
        if (id == null || id < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        RReadWriteLock readWriteLock = redisson.getReadWriteLock(RedisKeyConstants.LOCK_UPDATE_TEAM + id);
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        boolean b = false;
        try {
            long count = userTeamService.count(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, id));
            if (teamUpdateRequest.getMaxNum() < 2)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数最少为2");
            if (teamUpdateRequest.getMaxNum() < count)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不能小于当前队伍中的人数");
            Activity team = getOne(new QueryWrapper<Activity>().eq("id", id));
            if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
            //修改者是队长本人 || 修改者是管理员 才可以修改
            if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
                //不是管理员
                if (!team.getUserId().equals(loginUser.getId())) {
                    throw new BusinessException(ErrorCode.NO_AUTH, "只有队长和管理员才可以修改");
                }
            }
            if (teamUpdateRequest.equals(team)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能和原数据一致");
            }
            BeanUtils.copyProperties(teamUpdateRequest, team);
            b = updateById(team);
        } finally {
            rLock.unlock();
        }
        return b;
    }

    @Override
    public Boolean sendJoin(TeamSendJoinRequest teamSendJoinRequest, User loginUser) {
        if (Objects.isNull(teamSendJoinRequest)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        long teamId = teamSendJoinRequest.getTeamId();
        String userId = teamSendJoinRequest.getUserId();
        String password = teamSendJoinRequest.getPassword();
        if (!loginUser.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (teamId < 0 || StringUtils.isBlank(userId)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Activity team = getOne(new QueryWrapper<Activity>().eq("id", teamId));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR, "当前队伍不存在");
        String lockKey = RedisKeyConstants.LOCK_SEND_JOIN_TEAM + teamId;
        RLock lock = redisson.getLock(lockKey);
        lock.lock();
        try {
            UserActivity userTeam = userTeamService.checkOldUser(teamId, loginUser.getId());
            if (loginUser.getId().equals(team.getUserId())) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队长不可重复加入队伍");
            }
            if (team.getExpireTime().before(new Date())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入已过期队伍");
            }
            long count = userTeamService.count(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getUserId, userId).eq(UserActivity::getTeamId, teamId));
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止重复加入房间");
            }
            long teamCount = userTeamService.count(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getUserId, userId));
            if (teamCount >= 5) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "当前加入队伍已到达上线,vip~");
            }
            long currentNum = userTeamService.count(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, teamId));
            Integer maxNum = team.getMaxNum();
            if ((maxNum - currentNum) <= 0) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "当前队伍已满");
            }
            if (!Objects.isNull(userTeam)) {
                //用户之前存在
                boolean result = userTeamService.modifyIsDel(userTeam.getId());
                if (!result) throw new BusinessException(ErrorCode.NULL_ERROR);
                return result;
            }
            UserActivity resultUserTeam = new UserActivity();
            resultUserTeam.setTeamId(teamId);
            resultUserTeam.setUserId(userId);
            resultUserTeam.setJoinTime(new Date());
            boolean save = userTeamService.save(resultUserTeam);
            if (!save) throw new BusinessException(ErrorCode.NULL_ERROR, "加入队伍失败");
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        if (Objects.isNull(teamQuitRequest)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long teamId = teamQuitRequest.getTeamId();
        if (teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long count = userTeamService.count(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getUserId, loginUser.getId()).eq(UserActivity::getTeamId, teamId));
        if (count == 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "当前用户不在队伍中");
        }
        //校验信息
        List<UserActivity> userTeams = userTeamService.list(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, teamId));
        if (userTeams.size() == 1) {
            //解散队伍
            boolean b1 = userTeamService.remove(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, teamId).eq(UserActivity::getUserId, loginUser.getId()));
            if (!b1) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            boolean b = removeById(teamId);
            if (!b) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            return true;
        }
        Activity team = getOne(new QueryWrapper<Activity>().eq("id", teamId));
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (team.getUserId().equals(loginUser.getId())) {
            //队长逻辑
            userTeams = userTeams.stream().filter(userTeam -> {
                return !Objects.equals(userTeam.getUserId(), loginUser.getId());
            }).collect(Collectors.toList());
            userTeams.sort(Comparator.comparing(UserActivity::getJoinTime));
            UserActivity userTeam = userTeams.get(0);
            team.setUserId(userTeam.getUserId());
            boolean b = userTeamService.remove(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, teamId).eq(UserActivity::getUserId, loginUser.getId()));
            if (!b) throw new BusinessException(ErrorCode.NULL_ERROR);
            boolean result = updateById(team);
            if (!result) throw new BusinessException(ErrorCode.NULL_ERROR);
            return true;
        }
        //退出队伍
        boolean b = userTeamService.remove(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, teamId)
                .eq(UserActivity::getUserId, loginUser.getId()));
        if (!b) throw new BusinessException(ErrorCode.NULL_ERROR);
        //todo 获取当前队伍中的人数 退押金
        return b;
    }

    @Override
    //todo 分布式事务
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(long tid, User loginUser) {
        if (tid < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        Activity team = getOne(new LambdaQueryWrapper<Activity>().eq(Activity::getId, tid));
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前队伍不存在");
        }
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非队长不能移除房间");
        } else {
            boolean remove1 = userTeamService.remove(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getTeamId, tid));
            if (!remove1) throw new BusinessException(ErrorCode.NULL_ERROR);
            boolean remove = remove(new LambdaQueryWrapper<Activity>().eq(Activity::getId, tid));
            if (!remove) throw new BusinessException(ErrorCode.NULL_ERROR);
            return true;
        }
    }

    @Override
    public List<ActivityUserVo> getCreateTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String id = loginUser.getId();
        LambdaQueryWrapper<Activity> teamLambdaQueryWrapper = new LambdaQueryWrapper<Activity>().eq(Activity::getUserId, id);
        List<Activity> teams = this.list(teamLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<ActivityUserVo> teamUserVos = teams.stream().filter(team -> team.getExpireTime().after(new Date())).map(team -> {
            ActivityUserVo teamUserVo = new ActivityUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
        return teamUserVos;
    }

    @Override
    public List<ActivityUserVo> getExpireTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String id = loginUser.getId();
        LambdaQueryWrapper<Activity> teamLambdaQueryWrapper = new LambdaQueryWrapper<Activity>().eq(Activity::getUserId, id);
        List<Activity> teams = this.list(teamLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<ActivityUserVo> teamUserVos = teams.stream().filter(team -> team.getExpireTime().before(new Date())).map(team -> {
            ActivityUserVo teamUserVo = new ActivityUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
        return teamUserVos;
    }

    @Override
    public List<ActivityUserVo> getAddTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String userId = loginUser.getId();
        List<Activity> addTeamByUserId = teamMapper.getAddTeamByUserId(userId);
        if (CollectionUtils.isEmpty(addTeamByUserId)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return addTeamByUserId.stream().filter(team -> {
            if (team.getExpireTime().before(new Date())) {
                return false;
            }
            if (team.getUserId().equals(userId)) {
                return false;
            }
            return true;
        }).map(team -> {
            ActivityUserVo teamUserVo = new ActivityUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
    }

    @Override
    public ActivityUserVo getTeamInfoByTid(long teamId, User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        if (teamId < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String uid = loginUser.getId();
        Activity team = this.getOne(new LambdaQueryWrapper<Activity>().eq(Activity::getId, teamId));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            if (!team.getUserId().equals(loginUser.getId())) {
                UserActivity userTeam = userTeamService.getOne(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getUserId, uid).eq(UserActivity::getTeamId, teamId));
                //他不是这个队伍中成员
                if (Objects.isNull(userTeam)) throw new BusinessException(ErrorCode.NO_AUTH);
                //他是队伍中的一员
            }
        }
        ActivityUserVo teamUserVo = new ActivityUserVo();
        BeanUtils.copyProperties(team, teamUserVo);
        List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(teamId);
        if (CollectionUtils.isEmpty(userTeamByTeamId)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        teamUserVo.setTeamUserInfos(userTeamByTeamId);
        return teamUserVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delExpireTeam(long tid, User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        if (tid < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userId = loginUser.getId();
        //判断队伍是否存在
        Activity team = this.getOne(new LambdaQueryWrapper<Activity>().eq(Activity::getId, tid));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (team.getExpireTime().after(new Date())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "当前队伍未过期");
        }
        //判断当前权限
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            if (!team.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        //解散房间
        ActivityUserVo teamInfoByTid = getTeamInfoByTid(tid, loginUser);
        List<UserVo> teamUserInfos = teamInfoByTid.getTeamUserInfos();
        if (CollectionUtils.isEmpty(teamUserInfos)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        for (UserVo teamUserInfo : teamUserInfos) {
            String uid = teamUserInfo.getId();
            UserActivity userTeam = userTeamService.getOne(new LambdaQueryWrapper<UserActivity>().eq(UserActivity::getUserId, uid).eq(UserActivity::getTeamId, tid));
            userTeamService.removeById(userTeam.getId());
        }
        boolean remove = this.remove(new LambdaQueryWrapper<Activity>().eq(Activity::getId, tid));
        if (!remove) throw new BusinessException(ErrorCode.NULL_ERROR);
        return true;
    }
}




