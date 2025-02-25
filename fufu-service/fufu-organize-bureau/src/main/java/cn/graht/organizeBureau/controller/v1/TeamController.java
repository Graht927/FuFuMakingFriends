package cn.graht.organizeBureau.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.BusinessException;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.dtos.*;
import cn.graht.model.organizeBureau.pojos.Team;
import cn.graht.model.organizeBureau.vos.TeamUserVo;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.service.TeamService;
import cn.hutool.core.util.ObjectUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 队伍接口
 * @author  grhat
 */
@RestController
@RequestMapping("/v1/team")
@Slf4j
public class TeamController {
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public ResultApi<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (Objects.isNull(teamAddRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = getLoginUser();
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team,loginUser);
        //插入失败
        return ResultUtil.ok(teamId);
    }

    @GetMapping("/get")
    public ResultApi<Team> getTeam(long id) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (Objects.isNull(team)) {throw new BusinessException(ErrorCode.NULL_ERROR);}
        return ResultUtil.ok(team);
    }
    @GetMapping("/list")
    public ResultApi<List<TeamUserVo> > listTeamByPage(TeamQuery teamQuery, HttpServletRequest request) {
        if (Objects.isNull(teamQuery)) {throw new BusinessException(ErrorCode.PARAMS_ERROR);}
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        List<TeamUserVo> teamUserVos = teamService.listTeamByPage(teamQuery,request);
        return ResultUtil.ok(teamUserVos);
    }
    @PostMapping("/update")
    public ResultApi<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (Objects.isNull(teamUpdateRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = getLoginUser();
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result) {throw new BusinessException(ErrorCode.SYSTEM_ERROR);}
        return ResultUtil.ok(result);
    }
    @PostMapping("/sendJoin")
    public ResultApi<Boolean> joinTeam(@RequestBody TeamSendJoinRequest teamSendJoinRequest, HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        if (Objects.isNull(teamSendJoinRequest)) {throw new BusinessException(ErrorCode.PARAMS_ERROR);}
        Boolean result = teamService.sendJoin(teamSendJoinRequest,loginUser);
        return ResultUtil.ok(result);
    }
    @PostMapping("/quit")
    public ResultApi<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (Objects.isNull(teamQuitRequest)) {throw new BusinessException(ErrorCode.PARAMS_ERROR);}
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        if (!result) {throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出失败");}
        return ResultUtil.ok(result);
    }
    @PostMapping("/delete")
    public ResultApi<Boolean> deleteTeam(long tid,HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (tid < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean result  = teamService.deleteTeam(tid,loginUser);
        return ResultUtil.ok(result);
    }

    @GetMapping("/getCreateTeam")
    public ResultApi<List<TeamUserVo>> getCreateTeamByUser(HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        List<TeamUserVo> teamUserVos = teamService.getCreateTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/getExpireTeam")
    public ResultApi<List<TeamUserVo>> getExpireTeamByUser(HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        List<TeamUserVo> teamUserVos = teamService.getExpireTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/getAddTeam")
    public ResultApi<List<TeamUserVo>> getAddTeamByUser(HttpServletRequest request) {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        List<TeamUserVo> teamUserVos = teamService.getAddTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/info")
    public ResultApi<TeamUserVo> getTeamInfoByTid(long teamId,HttpServletRequest request) {
        if (teamId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        TeamUserVo teamUserVo = teamService.getTeamInfoByTid(teamId,loginUser);
        return ResultUtil.ok(teamUserVo);
    }
    @PostMapping("/delExpireTeam")
    public ResultApi<Boolean> delExpireTeam(@RequestBody(required = true) Long teamId,HttpServletRequest request) {
        if (teamId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        Boolean delResult = teamService.delExpireTeam(teamId,loginUser);
        return ResultUtil.ok(delResult);
    }
    private User getLoginUser(){
        String loginId = (String) StpUtil.getLoginId();
        ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(loginId);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userInfo)||ObjectUtil.isEmpty(userInfo.getData()), ErrorCode.NOT_LOGIN_ERROR);
        UserVo loginUser = userInfo.getData();
        User user = new User();
        BeanUtils.copyProperties(user,loginUser);
        return user;
    }


     /*@PostMapping("/update")
    public ResultApi<Boolean> updateTeam(@RequestBody Team team) {
        if (Objects.isNull(team)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.updateById(team);
        if (!result) {throw new BusinessException(ErrorCode.SYSTEM_ERROR);}
        return ResultUtil.ok(result);
    }*/

    /*@GetMapping("/list")
    public ResultApi<Page<Team>> listTeam(TeamQuery teamQuery) {
        if (Objects.isNull(teamQuery)) {throw new BusinessException(ErrorCode.PARAMS_ERROR);}
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> resultPage = teamService.page(new Page<Team>(teamQuery.getPageNum(), teamQuery.getPageSize()), new QueryWrapper<Team>(team));
        return ResultUtil.ok(resultPage);
    }*/
}
