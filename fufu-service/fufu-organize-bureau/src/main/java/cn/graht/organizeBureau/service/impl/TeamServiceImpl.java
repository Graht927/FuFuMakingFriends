package cn.graht.organizeBureau.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.RedisKeyConstants;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.constant.UserConstant;
import cn.graht.common.exception.BusinessException;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.dtos.TeamQuitRequest;
import cn.graht.model.organizeBureau.dtos.TeamUpdateRequest;
import cn.graht.model.organizeBureau.pojos.Team;
import cn.graht.model.organizeBureau.dtos.TeamQuery;
import cn.graht.model.organizeBureau.dtos.TeamSendJoinRequest;
import cn.graht.model.organizeBureau.vos.TeamUserVo;
import cn.graht.model.organizeBureau.pojos.UserTeam;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.enums.TeamStatusEnum;
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
        ServiceImpl<TeamMapper, Team>
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
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User user) {
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (Objects.isNull(user)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        final String userId = user.getId();
        //校验信息
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 15) throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() >= 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        String description = team.getDescription();
        if (StringUtils.isBlank(description) && description.length() > 512)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述信息不符合要求");
        int status = Optional.ofNullable(team.getStatus()).orElse(-1);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByVal(status);
        if (statusEnum == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        if (TeamStatusEnum.SECRET.equals(statusEnum) && StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 32)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码格式错误");
        if (TeamStatusEnum.SECRET.equals(statusEnum) && !StringUtils.isBlank(team.getPassword()) && team.getPassword().length() < 32) {
            team.setPassword(DigestUtils.md5DigestAsHex((SystemConstant.SALT + team.getPassword()).getBytes()));
        }
        if (!TeamStatusEnum.SECRET.equals(statusEnum)) team.setPassword(null);
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间 > 当前时间");
        }
        //todo 有bug 并发安全
        long count = count(new QueryWrapper<Team>().eq("userId", userId));
        if (count >= 5)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前队伍数量已经是最大值，如需新建请查看vip");
        team.setId(null);
        team.setUserId(userId);
        boolean save = save(team);
        if (!save) throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        Long teamId = team.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean save1 = userTeamService.save(userTeam);
        if (!save1) throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        return team.getId();
    }

    @Override
    public List<TeamUserVo> listTeamByPage(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        long tid = Optional.ofNullable(teamQuery.getId()).orElse(0l);
        long pageNum = teamQuery.getPageNum();
        long pageSize = teamQuery.getPageSize();
        String name = teamQuery.getName();
        String searchText = teamQuery.getSearchText();
        String description = teamQuery.getDescription();
        Integer maxNum = teamQuery.getMaxNum();
        String userId = teamQuery.getUserId();
        Integer status = teamQuery.getStatus();
        if (pageNum < 0 || pageSize <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        //通过name来查询
        //分页
        IPage<Team> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<Team>();
        if (!Objects.isNull(teamQuery.getExpireTime())) {
            queryWrapper.gt(Team::getExpireTime, DateUtil.now());
        }
        //判断条件
        if (!StringUtils.isBlank(name)) {
            queryWrapper.like(Team::getName, name);
        }
        if (!StringUtils.isBlank(searchText)) {
            queryWrapper.like(Team::getName, searchText).or().like(Team::getDescription, searchText);
        }
        if (tid > 0) {
            queryWrapper.eq(Team::getId, tid);
        }
        if (!StringUtils.isBlank(description)) {
            queryWrapper.like(Team::getDescription, description);
        }
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq(Team::getMaxNum, maxNum);
        }
        if (userId != null) {
            queryWrapper.eq(Team::getUserId, userId);
        }
        User loginUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (status != null) {
            if (!TeamStatusEnum.SECRET.getValue().equals(status) && !TeamStatusEnum.PRIVATE.getValue().equals(status)) {
                queryWrapper.eq(Team::getStatus, status);
            } else {
                if (!Objects.isNull(loginUser)) {
                    teamQuery.setUserId(loginUser.getId());
                    if (userId != null) {
                        if (userId.equals(loginUser.getId())) {
                            queryWrapper.eq(Team::getStatus, status);
                        } else {
                            if (TeamStatusEnum.PRIVATE.getValue().equals(status) && UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
                                queryWrapper.eq(Team::getStatus, status);
                            } else throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "不可以查看别人的私有房间噢");
                        }
                    } else {
                        if (!TeamStatusEnum.PRIVATE.getValue().equals(status)) {
                            queryWrapper.eq(Team::getStatus, status);
                        } else throw new BusinessException(ErrorCode.NO_AUTH);
                    }
                } else queryWrapper.eq(Team::getStatus, status);
            }
        }
        IPage<Team> page1 = teamMapper.selectPage(page, queryWrapper);
        List<Team> records = page1.getRecords();
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
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            Long id = team.getId();
            if (id == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(id);
            if (CollectionUtils.isEmpty(userTeamByTeamId)) throw new BusinessException(ErrorCode.NULL_ERROR);
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
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
        if (Objects.isNull(teamUpdateRequest)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
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
            long count = userTeamService.count(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, id));
            if (teamUpdateRequest.getMaxNum() < 2)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数最少为2");
            if (teamUpdateRequest.getMaxNum() < count)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不能小于当前队伍中的人数");
            Team team = getOne(new QueryWrapper<Team>().eq("id", id));
            if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
            if (TeamStatusEnum.SECRET.getValue().equals(teamUpdateRequest.getStatus()) && StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须有密码");
            }
            if (!TeamStatusEnum.SECRET.getValue().equals(teamUpdateRequest.getStatus())) {
                teamUpdateRequest.setPassword(null);
            }
            //如果之前的状态是加密的 当前的状态不是加密的就将数据库中的password设置为空
            if (TeamStatusEnum.SECRET.getValue().equals(team.getStatus()) && !TeamStatusEnum.SECRET.getValue().equals(teamUpdateRequest.getStatus())) {
                teamUpdateRequest.setPassword("");
            }
            if (TeamStatusEnum.SECRET.getValue().equals(teamUpdateRequest.getStatus()) && !StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                teamUpdateRequest.setPassword(DigestUtils.md5DigestAsHex((SystemConstant.SALT + teamUpdateRequest.getPassword()).getBytes()));
                if (TeamStatusEnum.SECRET.getValue().equals(teamUpdateRequest.getStatus()) && TeamStatusEnum.SECRET.getValue().equals(team.getStatus())) {
                    if (!StringUtils.isBlank(team.getPassword()) && team.getPassword().equals(teamUpdateRequest.getPassword())) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码和新密码一致");
                    }
                }
            }
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
        Team team = getOne(new QueryWrapper<Team>().eq("id", teamId));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR, "当前队伍不存在");
        //为了防止并发安全问题 加锁 todo 实现
        String lockKey = RedisKeyConstants.LOCK_SEND_JOIN_TEAM + teamId;
        RLock lock = redisson.getLock(lockKey);
        lock.lock();
        try {
            UserTeam userTeam = userTeamService.checkOldUser(teamId, loginUser.getId());

            if (TeamStatusEnum.PRIVATE.getValue().equals(team.getStatus())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "私有队伍禁止加入");
            }
            if (loginUser.getId().equals(team.getUserId())) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队长不可重复加入队伍");
            }
            if (team.getExpireTime().before(new Date())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入已过期队伍");
            }
            long count = userTeamService.count(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getUserId, userId).eq(UserTeam::getTeamId, teamId));
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止重复加入房间");
            }
            //当这个房间是加密的房间 得输入密码来校验
            if (TeamStatusEnum.SECRET.getValue().equals(team.getStatus())) {
                if (StringUtils.isBlank(password)) throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能为空");
                if (!team.getPassword().equals(DigestUtils.md5DigestAsHex((SystemConstant.SALT + password).getBytes()))) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
                }
            }
            long teamCount = userTeamService.count(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getUserId, userId));
            if (teamCount >= 5) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "当前加入队伍已到达上线,vip~");
            }
            long currentNum = userTeamService.count(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, teamId));
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
            UserTeam resultUserTeam = new UserTeam();
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
        long count = userTeamService.count(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getUserId, loginUser.getId()).eq(UserTeam::getTeamId, teamId));
        if (count == 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "当前用户不在队伍中");
        }
        //校验信息
        List<UserTeam> userTeams = userTeamService.list(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, teamId));
        if (userTeams.size() == 1) {
            //解散队伍
            boolean b1 = userTeamService.remove(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, loginUser.getId()));
            if (!b1) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            boolean b = removeById(teamId);
            if (!b) throw new BusinessException(ErrorCode.PARAMS_ERROR);
            return true;
        }
        Team team = getOne(new QueryWrapper<Team>().eq("id", teamId));
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (team.getUserId().equals(loginUser.getId())) {
            //队长逻辑
            userTeams = userTeams.stream().filter(userTeam -> {
                return !Objects.equals(userTeam.getUserId(), loginUser.getId());
            }).collect(Collectors.toList());
            userTeams.sort(Comparator.comparing(UserTeam::getJoinTime));
            UserTeam userTeam = userTeams.get(0);
            team.setUserId(userTeam.getUserId());
            boolean b = userTeamService.remove(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, teamId).eq(UserTeam::getUserId, loginUser.getId()));
            if (!b) throw new BusinessException(ErrorCode.NULL_ERROR);
            boolean result = updateById(team);
            if (!result) throw new BusinessException(ErrorCode.NULL_ERROR);
            return true;
        }
        //退出队伍
        boolean b = userTeamService.remove(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, teamId)
                .eq(UserTeam::getUserId, loginUser.getId()));
        if (!b) throw new BusinessException(ErrorCode.NULL_ERROR);
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTeam(long tid, User loginUser) {
        if (tid < 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        Team team = getOne(new LambdaQueryWrapper<Team>().eq(Team::getId, tid));
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前队伍不存在");
        }
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            if (!team.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "非队长不能移除房间");
            }
            throw new BusinessException(ErrorCode.NO_AUTH, "非队长不可移除");
        }
        //移除队伍
        boolean remove1 = userTeamService.remove(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getTeamId, tid));
        if (!remove1) throw new BusinessException(ErrorCode.NULL_ERROR);
        boolean remove = remove(new LambdaQueryWrapper<Team>().eq(Team::getId, tid));
        if (!remove) throw new BusinessException(ErrorCode.NULL_ERROR);

        return true;
    }

    @Override
    public List<TeamUserVo> getCreateTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String id = loginUser.getId();
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper  = new LambdaQueryWrapper<Team>().eq(Team::getUserId, id);
        List<Team> teams = this.list(teamLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<TeamUserVo> teamUserVos = teams.stream().filter(team -> team.getExpireTime().after(new Date())).map(team -> {
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
        return teamUserVos;
    }

    @Override
    public List<TeamUserVo> getExpireTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String id = loginUser.getId();
        LambdaQueryWrapper<Team> teamLambdaQueryWrapper  = new LambdaQueryWrapper<Team>().eq(Team::getUserId, id);
        List<Team> teams = this.list(teamLambdaQueryWrapper);
        if (CollectionUtils.isEmpty(teams)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        List<TeamUserVo> teamUserVos = teams.stream().filter(team -> team.getExpireTime().before(new Date())).map(team -> {
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
        return teamUserVos;
    }
    @Override
    public List<TeamUserVo> getAddTeamByUser(User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        String userId = loginUser.getId();
        List<Team> addTeamByUserId = teamMapper.getAddTeamByUserId(userId);
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
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            List<UserVo> userTeamByTeamId = userTeamService.findUserTeamByTeamId(team.getId());
            teamUserVo.setTeamUserInfos(userTeamByTeamId);
            return teamUserVo;
        }).collect(Collectors.toList());
    }

    @Override
    public TeamUserVo getTeamInfoByTid(long teamId,User loginUser) {
        if (Objects.isNull(loginUser)) throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        if (teamId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String uid = loginUser.getId();
        Team team =  this.getOne(new LambdaQueryWrapper<Team>().eq(Team::getId, teamId));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.NULL_ERROR);
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            if (!team.getUserId().equals(loginUser.getId())){
                UserTeam userTeam = userTeamService.getOne(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getUserId, uid).eq(UserTeam::getTeamId, teamId));
                //他不是这个队伍中成员
                if (Objects.isNull(userTeam)) throw new BusinessException(ErrorCode.NO_AUTH);
                //他是队伍中的一员
            }
        }
        TeamUserVo teamUserVo = new TeamUserVo();
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
        if (tid<0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        String userId = loginUser.getId();
        //判断队伍是否存在
        Team team = this.getOne(new LambdaQueryWrapper<Team>().eq(Team::getId, tid));
        if (Objects.isNull(team)) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        if (team.getExpireTime().after(new Date())){
            throw new BusinessException(ErrorCode.NO_AUTH,"当前队伍未过期");
        }
        //判断当前权限
        if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
            if (!team.getUserId().equals(userId)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }
        //解散房间
        TeamUserVo teamInfoByTid = getTeamInfoByTid(tid, loginUser);
        List<UserVo> teamUserInfos = teamInfoByTid.getTeamUserInfos();
        if (CollectionUtils.isEmpty(teamUserInfos)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        for (UserVo teamUserInfo : teamUserInfos) {
            String uid = teamUserInfo.getId();
            UserTeam userTeam = userTeamService.getOne(new LambdaQueryWrapper<UserTeam>().eq(UserTeam::getUserId, uid).eq(UserTeam::getTeamId, tid));
            userTeamService.removeById(userTeam.getId());
        }
        boolean remove = this.remove(new LambdaQueryWrapper<Team>().eq(Team::getId, tid));
        if (!remove) throw new BusinessException(ErrorCode.NULL_ERROR);
        return true;
    }
}




