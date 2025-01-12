package cn.graht.user.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.constant.UserConstant;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.user.service.UserService;
import cn.graht.common.exception.ThrowUtils;
import cn.hutool.core.util.ReUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1")
@Tag(name = "用户注册",description = "用户注册controller")
public class UserRegisterController {

    @Resource
    private UserService userService;

    @PostMapping("/register auth")
    @Operation(summary = "注册",description = "注册成功 跳转登录")
    @ApiResponse(responseCode = "200",description = "注册成功 返回ok")
    public ResultApi register(@RequestBody RegisterDto registerDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(registerDto)
                        || StringUtils.isBlank(registerDto.getNickname())
                        || StringUtils.isBlank(registerDto.getPhone())
                        || StringUtils.isBlank(registerDto.getPhoneCode())
                        || StringUtils.isBlank(registerDto.getUserPassword())
                        || StringUtils.isBlank(registerDto.getCheckPassword())
                , ErrorCode.LOGIN_PARAMS_ERROR);
        int nicknameLength = registerDto.getNickname().length();
        int phoneCodeLength = registerDto.getPhoneCode().length();
        ThrowUtils.throwIf(nicknameLength<3||nicknameLength>8,ErrorCode.REGISTER_PARAMS_ERROR);
        ThrowUtils.throwIf(!ReUtil.isMatch(UserConstant.PHONE_NUMBER_PATTERN,registerDto.getPhone()),ErrorCode.REGISTER_PARAMS_ERROR);
        ThrowUtils.throwIf(phoneCodeLength != 6,ErrorCode.REGISTER_PARAMS_ERROR);
        ThrowUtils.throwIf(!ReUtil.isMatch(UserConstant.PASSWORD_PATTERN,registerDto.getUserPassword()) || !ReUtil.isMatch(UserConstant.PASSWORD_PATTERN,registerDto.getUserPassword()),ErrorCode.REGISTER_PARAMS_ERROR);
        ThrowUtils.throwIf(userService.register(registerDto),ErrorCode.REGISTER_PARAMS_ERROR);
        return ResultUtil.ok();
    }
}
