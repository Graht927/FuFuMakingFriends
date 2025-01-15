package cn.graht.user.controller.v1;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.UserConstant;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.user.aop.anno.AddrToParam;
import cn.graht.user.event.FuFuEventPublisher;
import cn.hutool.core.util.ReUtil;
import cn.graht.common.exception.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import cn.graht.user.service.UserService;


/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "用户登录",description = "用户登录controller")
public class UserLoginController {

    private static final Logger log = LoggerFactory.getLogger(UserLoginController.class);
    @Resource
    private UserService userService;

    @PostMapping("/login auth")
    @Operation(summary = "登录",description = "根据手机号和密码进行登录")
    @ApiResponse(responseCode = "200",description = "登录成功 返回token")
    @ApiResponse(responseCode = "40101",description = "用户名或密码错误")
    @AddrToParam
    public ResultApi login(@RequestBody LoginDto loginDto) {
        String addr = loginDto.getAddr();
        log.info("addr: {}",addr);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(loginDto)
                || StringUtils.isBlank(loginDto.getPhone())
                || StringUtils.isBlank(loginDto.getPhoneCode())
                || StringUtils.isBlank(loginDto.getUserPassword()), ErrorCode.LOGIN_PARAMS_ERROR);
        String phoneCode = loginDto.getPhoneCode();
        ThrowUtils.throwIf(phoneCode.length() != 6,ErrorCode.LOGIN_PARAMS_ERROR);
        String userPassword = loginDto.getUserPassword();
        ThrowUtils.throwIf(!ReUtil.isMatch(UserConstant.PASSWORD_PATTERN,userPassword),ErrorCode.LOGIN_PARAMS_ERROR);
        SaTokenInfo saTokenInfo = userService.login(loginDto);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(saTokenInfo),ErrorCode.NULL_ERROR);
        return ResultUtil.ok(saTokenInfo);
    }

}
