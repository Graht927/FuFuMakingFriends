package cn.graht.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.constant.UserConstant;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.model.user.pojos.User;
import cn.graht.user.mapper.UserMapper;
import cn.graht.user.service.UserService;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.graht.common.constant.SystemConstant;
import cn.graht.common.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author GRAHT
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-01-12 10:42:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Override
    public SaTokenInfo login(LoginDto loginDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(loginDto) ||
                StringUtils.isAnyBlank(loginDto.getUserPassword(),loginDto.getPhone()),
                ErrorCode.LOGIN_PARAMS_ERROR);
        String userPassword = DigestUtils.md5DigestAsHex((SystemConstant.SALT+loginDto.getUserPassword()).getBytes());
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, loginDto.getPhone()).eq(User::getUserPassword, userPassword));
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user),ErrorCode.LOGIN_PARAMS_ERROR);
        StpUtil.login(user.getId());
        return StpUtil.getTokenInfo();
    }

    @Override
    public boolean register(RegisterDto registerDto) {
        //校验数据
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
        //判断二次密码是否一致
        ThrowUtils.throwIf( !registerDto.getUserPassword().equals(registerDto.getCheckPassword()), ErrorCode.REGISTER_PASSWORD_ERROR);
        //判断该手机号是否已注册
        long phoneCount = count(new LambdaQueryWrapper<User>().eq(User::getPhone, registerDto.getPhone()));
        ThrowUtils.throwIf(phoneCount == 1, ErrorCode.REGISTER_PHONE_ERROR);
        //判断nickname是否已存在
        long nicknameCount = count(new LambdaQueryWrapper<User>().eq(User::getNickname, registerDto.getNickname()));
        ThrowUtils.throwIf(nicknameCount == 1, ErrorCode.REGISTER_PHONE_ERROR);
        //否 => 判断验证码是否正确

        return false;
    }
}




