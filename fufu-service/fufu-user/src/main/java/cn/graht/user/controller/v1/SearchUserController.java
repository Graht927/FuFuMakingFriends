package cn.graht.user.controller.v1;

import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.model.user.vos.UserVo;
import cn.graht.model.user.dtos.RandomGetUserDto;
import cn.graht.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/user/search")
public class SearchUserController {
    @Resource
    private UserService userService;
    @PostMapping
    public ResultApi<List<UserVo>> randomGetUserVo(@RequestBody RandomGetUserDto randomGetUserDto){
        return ResultUtil.ok(userService.randomGetUserVo(randomGetUserDto));
    }
}
