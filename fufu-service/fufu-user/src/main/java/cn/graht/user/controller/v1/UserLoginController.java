package cn.graht.user.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.ErrorCode;
import cn.graht.common.ResultApi;
import cn.graht.common.ResultUtil;
import cn.graht.model.user.dtos.LoginDto;
import exception.ThrowUtils;
import io.netty.util.internal.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.Name;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "用户登录",description = "用户登录controller")
public class UserLoginController {

    @PostMapping("/login auth")
    @Operation(summary = "登录",description = "根据手机号和密码进行登录")
    @ApiResponse(responseCode = "200",description = "登录成功 返回token")
    @ApiResponse(responseCode = "40101",description = "用户名或密码错误")
    public ResultApi login(@RequestBody LoginDto loginDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(loginDto) || StringUtils.isBlank(loginDto.getPhone())
                || StringUtils.isBlank(loginDto.getPassword()), ErrorCode.LOGIN_PARAMS_ERROR);
        //todo 正式验证 生成uuid
        StpUtil.login(12312);
        return ResultUtil.ok(StpUtil.getTokenInfo());
    }
}
