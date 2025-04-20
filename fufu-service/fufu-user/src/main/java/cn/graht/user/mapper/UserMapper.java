package cn.graht.user.mapper;

import cn.graht.model.user.dtos.RandomGetUserDto;
import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import cn.graht.user.controller.v1.SearchUserController;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2025-01-12 10:42:19
*/
public interface UserMapper extends BaseMapper<User> {

    List<UserVo> randomGetUserVo(RandomGetUserDto randomGetUserDto, Integer size);
}




