package cn.graht.user.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.graht.model.user.dtos.LoginDto;
import cn.graht.model.user.dtos.RegisterDto;
import cn.graht.model.user.pojos.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author GRAHT
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-01-12 10:42:19
*/
public interface UserService extends IService<User> {
    SaTokenInfo login(LoginDto loginDto);

    boolean register(RegisterDto registerDto);
}
