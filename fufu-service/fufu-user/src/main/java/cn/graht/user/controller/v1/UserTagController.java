package cn.graht.user.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.user.pojos.User;
import cn.graht.user.service.UserService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GRAHT
 * 用户标签Controller
 */
@RestController
@RequestMapping("/v1/tags")
@Tag(name = "用户标签",description = "用户标签Controller")
public class UserTagController {
    @Resource
    private UserService userService;
    @GetMapping("/{uid}")
    @Operation(summary = "获取用户标签",description = "通过用户id获取用户标签")
    @ApiResponse(responseCode = "200",description = "注册成功 返回用户的标签")
    public ResultApi<List<String>> getUserTags(@PathVariable String uid) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = userService.getOne(lambdaQueryWrapper.eq(User::getId, uid));
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.NULL_ERROR);
        String tags = user.getTags();
        List<String> str = JSONUtil.toList(tags, String.class);
        return ResultUtil.ok(str);
    }
    @PutMapping("/{uid}")
    @Operation(summary = "修改用户标签",description = "修改用户标签")
    @ApiResponse(responseCode = "200",description = "返回信息")
    @ApiResponse(responseCode = "40000",description = "参数错误")
    @ApiResponse(responseCode = "40002",description = "结果为空")
    public ResultApi<Void> editUserTags(@PathVariable String uid,@RequestBody List<String> tags) {
        ThrowUtils.throwIf(StringUtils.isBlank(uid), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(CollectionUtil.isEmpty(tags),ErrorCode.PARAMS_ERROR);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = userService.getOne(lambdaQueryWrapper.eq(User::getId, uid));
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.NULL_ERROR);
        user.setTags(JSONUtil.toJsonStr(tags));
        boolean b = userService.updateById(user);
        ThrowUtils.throwIf(!b,ErrorCode.OPERATION_ERROR);
        return ResultUtil.ok();
    }
}
