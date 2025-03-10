package cn.graht.organizeBureau.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.BusinessException;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.organizeBureau.dtos.*;
import cn.graht.model.organizeBureau.pojos.Activity;
import cn.graht.model.organizeBureau.vos.ActivityUserVo;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.organizeBureau.service.TeamService;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "活动|组局", description = "活动|组局Controller")
@Slf4j
public class TeamController {
    @Resource
    private UserFeignApi userFeignApi;
    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    @Operation(summary = "创建队伍", description = "创建队伍")
    @ApiResponse(responseCode = "200", description = "boolean")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    public ResultApi<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamAddRequest), ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        Activity activity = new Activity();
        BeanUtils.copyProperties(teamAddRequest,activity);
        long teamId = teamService.addTeam(activity,loginUser);
        //插入失败
        return ResultUtil.ok(teamId);
    }

    @GetMapping("/get")
    @Operation(summary = "获取队伍信息", description = "获取队伍信息")
    @ApiResponse(responseCode = "200", description = "Team")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    @ApiResponse(responseCode = "40002", description = "请求结果为空")
    public ResultApi<Activity> getTeam(long id) {
        ThrowUtils.throwIf(id < 0,ErrorCode.PARAMS_ERROR);
        Activity team = teamService.getById(id);
        ThrowUtils.throwIf(Objects.isNull(team),ErrorCode.NULL_ERROR);
        return ResultUtil.ok(team);
    }
    @GetMapping("/list")
    public ResultApi<List<ActivityUserVo> > listTeamByPage(TeamQuery teamQuery, HttpServletRequest request) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(teamQuery),ErrorCode.PARAMS_ERROR);
        Activity team = new Activity();
        BeanUtils.copyProperties(teamQuery,team);
        List<ActivityUserVo> teamUserVos = teamService.listTeamByPage(teamQuery,request);
        return ResultUtil.ok(teamUserVos);
    }
    @PostMapping("/update")
    @Operation(summary = "更新队伍", description = "更新队伍")
    @ApiResponse(responseCode = "200", description = "boolean")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(Objects.isNull(teamUpdateRequest),ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR);
        return ResultUtil.ok(result);
    }
    @PostMapping("/sendJoin")
    @Operation(summary = "发送加入队伍请求", description = "发送加入队伍请求")
    @ApiResponse(responseCode = "200", description = "boolean")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> joinTeam(@RequestBody TeamSendJoinRequest teamSendJoinRequest, HttpServletRequest request) {
        User loginUser = getLoginUser();
        ThrowUtils.throwIf(Objects.isNull(teamSendJoinRequest),ErrorCode.PARAMS_ERROR);
        Boolean result = teamService.sendJoin(teamSendJoinRequest,loginUser);
        return ResultUtil.ok(result);
    }
    @PostMapping("/quit")
    @Operation(summary = "退出队伍", description = "退出队伍")
    @ApiResponse(responseCode = "200", description = "boolean")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest) {
        ThrowUtils.throwIf(Objects.isNull(teamQuitRequest),ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        ThrowUtils.throwIf(!result,ErrorCode.SYSTEM_ERROR,"退出失败");
        return ResultUtil.ok(true);
    }
    @PostMapping("/delete")
    @Operation(summary = "删除队伍", description = "删除队伍")
    @ApiResponse(responseCode = "200", description = "boolean")
    @ApiResponse(responseCode = "40000", description = "请求参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> deleteTeam(long tid) {
        ThrowUtils.throwIf(tid < 0,ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        Boolean result  = teamService.deleteTeam(tid,loginUser);
        return ResultUtil.ok(result);
    }

    @GetMapping("/getCreateTeam")
    public ResultApi<List<ActivityUserVo>> getCreateTeamByUser() {
        User loginUser = getLoginUser();
        List<ActivityUserVo> teamUserVos = teamService.getCreateTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/getExpireTeam")
    public ResultApi<List<ActivityUserVo>> getExpireTeamByUser() {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        List<ActivityUserVo> teamUserVos = teamService.getExpireTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/getAddTeam")
    public ResultApi<List<ActivityUserVo>> getAddTeamByUser() {
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        List<ActivityUserVo> teamUserVos = teamService.getAddTeamByUser(loginUser);
        return ResultUtil.ok(teamUserVos);
    }
    @GetMapping("/info")
    public ResultApi<ActivityUserVo> getTeamInfoByTid(long teamId) {
        if (teamId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User loginUser = getLoginUser();
        if (Objects.isNull(loginUser)) {throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);}
        ActivityUserVo teamUserVo = teamService.getTeamInfoByTid(teamId,loginUser);
        return ResultUtil.ok(teamUserVo);
    }
    @PostMapping("/delExpireTeam")
    public ResultApi<Boolean> delExpireTeam(@RequestBody(required = true) Long teamId) {
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
