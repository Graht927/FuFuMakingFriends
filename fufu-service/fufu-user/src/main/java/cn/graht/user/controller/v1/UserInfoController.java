package cn.graht.user.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.user.dtos.EditUserInfoDto;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.service.UserService;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author GRAHT
 */

@RestController
@RequestMapping("/v1")
@Tag(name = "用户信息",description = "用户信息Controller")
public class UserInfoController {
    @Resource
    private UserService userService;

    @GetMapping("/info/{uid}")
    @Operation(summary = "通过id获取",description = "通过id获取用户信息")
    @ApiResponse(responseCode = "200",description = "返回信息")
    @ApiResponse(responseCode = "40000",description = "参数错误")
    @ApiResponse(responseCode = "40002",description = "结果为空")
    public ResultApi<UserVo> getUserInfo(@PathVariable @Schema(description = "用户id") String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, uid));
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user),ErrorCode.NULL_ERROR);
        UserVo userVo = UserVo.objToVo(user);
        return ResultUtil.ok(userVo);
    }

    @PutMapping("/info}")
    @Operation(summary = "修改用户",description = "修改用户详情")
    @ApiResponse(responseCode = "200",description = "返回信息")
    @ApiResponse(responseCode = "40000",description = "参数错误")
    @ApiResponse(responseCode = "40002",description = "结果为空")
    public ResultApi<UserVo> editUserInfo(EditUserInfoDto editUserInfoDto) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(editUserInfoDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(editUserInfoDto.getId()), ErrorCode.PARAMS_ERROR);
        UserVo userVo = userService.editUserInfo(editUserInfoDto);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userVo),ErrorCode.NULL_ERROR);
        return ResultUtil.ok(userVo);
    }
    @DeleteMapping("/info/{uid}")
    @Operation(summary = "注销用户",description = "通过id注销用户")
    @ApiResponse(responseCode = "200",description = "返回信息")
    @ApiResponse(responseCode = "40000",description = "参数错误")
    @ApiResponse(responseCode = "40002",description = "结果为空")
    public ResultApi<Boolean> logOutOfUser(@PathVariable String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        boolean remove = userService.remove(new LambdaQueryWrapper<User>().eq(User::getId, uid));
        ThrowUtils.throwIf(!remove,ErrorCode.NULL_ERROR);
        return ResultUtil.ok(true);
    }

}
