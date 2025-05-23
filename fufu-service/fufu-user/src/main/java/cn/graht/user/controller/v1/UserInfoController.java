package cn.graht.user.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.user.dtos.EditUserInfoDto;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.boot.UserRedissonCache;
import cn.graht.user.mq.producer.UserUnregisterProducer;
import cn.graht.user.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GRAHT
 */

@RestController
@RequestMapping("/v1")
@Tag(name = "用户信息", description = "用户信息Controller")
public class UserInfoController {
    @Resource
    private UserService userService;
    @Resource
    private UserUnregisterProducer userUnregisterProducer;
    @Resource
    private UserRedissonCache userRedissonCache;

    @GetMapping("/info/{uid}")
    @Operation(summary = "通过id获取", description = "通过id获取用户信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<UserVo> getUserInfo(@PathVariable @Schema(description = "用户id") String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        String user1 = userRedissonCache.getUser(uid);
        if (StringUtils.isNotBlank(user1)) {
            UserVo userVo = JSONUtil.toBean(user1, UserVo.class);
            return ResultUtil.ok(userVo);
        }
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, uid));
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.NULL_ERROR);
        UserVo userVo = UserVo.objToVo(user);
        userRedissonCache.addUser(uid, JSONUtil.toJsonStr(userVo));
        return ResultUtil.ok(userVo);
    }

    @PutMapping("/info")
    @Operation(summary = "修改用户", description = "修改用户详情")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<UserVo> editUserInfo(@RequestBody EditUserInfoDto editUserInfoDto) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(editUserInfoDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(editUserInfoDto.getId()), ErrorCode.PARAMS_ERROR);
        UserVo userVo = userService.editUserInfo(editUserInfoDto);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userVo), ErrorCode.NULL_ERROR);
        return ResultUtil.ok(userVo);
    }

    @DeleteMapping("/info/{uid}")
    @Operation(summary = "注销用户", description = "通过id注销用户")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Boolean> logOutOfUser(@PathVariable String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        userUnregisterProducer.sendUnregisterRequest(uid);
        return ResultUtil.ok(true);
    }

    @GetMapping("/info/cancel-unregister/{uid}")
    @Operation(summary = "取消注销用户", description = "通过id取消注销用户")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> cancelUnregisterUser(@PathVariable String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        // 实现取消注销逻辑，例如从消息队列中移除消息
        boolean canceled = userService.cancelUnregisterRequest(uid);
        ThrowUtils.throwIf(!canceled, ErrorCode.NULL_ERROR);
        return ResultUtil.ok(true);
    }

    @DeleteMapping("/unregisterList/{uid}")
    @Operation(summary = "移除注销用户", description = "通过id移除注销用户")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> removeUnregisterUser(@PathVariable String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        boolean removed = userService.UnregisterRemoveById(uid);
        return ResultUtil.ok(removed);
    }

    @PostMapping("/initGetUserIds")
    @Operation(summary = "获取所有用户id", description = "获取所有用户id")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<UserIdsVo> getAllUserId(@RequestBody PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(pageQuery), ErrorCode.PARAMS_ERROR);
        return ResultUtil.ok(userService.getAllUserId(pageQuery));
    }

    @GetMapping("/logout")
    @Operation(summary = "退出登录", description = "退出用户登录信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    public ResultApi<Boolean> logout() {
        try {
            String loginId = (String) StpUtil.getLoginId();
            ThrowUtils.throwIf(StringUtils.isBlank(loginId), ErrorCode.NOT_LOGIN_ERROR);
            StpUtil.logout(loginId);
        } catch (Exception e) {
            return ResultUtil.error(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtil.ok(true);
    }

}
