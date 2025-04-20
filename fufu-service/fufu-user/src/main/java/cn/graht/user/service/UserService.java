package cn.graht.user.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.graht.common.commons.PageQuery;
import cn.graht.model.user.dtos.EditUserInfoDto;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.dtos.RandomGetUserDto;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserIdsVo;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.controller.v1.SearchUserController;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-12 10:42:19
*/
public interface UserService extends IService<User> {
    SaTokenInfo login(LoginDto loginDto);

    boolean register(RegisterDto registerDto);

    UserVo editUserInfo(EditUserInfoDto editUserInfoDto);

    boolean cancelUnregisterRequest(String uid);

    void sendUnregisterRequest(String uid);

    boolean UnregisterRemoveById(String uid);

    UserIdsVo getAllUserId(PageQuery pageQuery);

    List<UserVo> randomGetUserVo(RandomGetUserDto randomGetUserDto);
}
