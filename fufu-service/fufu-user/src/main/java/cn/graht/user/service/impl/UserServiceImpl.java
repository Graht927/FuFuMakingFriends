package cn.graht.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.ErrorCode;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.pojos.User;
import cn.graht.user.mapper.UserMapper;
import cn.graht.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import constant.SystemConstant;
import exception.ThrowUtils;
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
}




