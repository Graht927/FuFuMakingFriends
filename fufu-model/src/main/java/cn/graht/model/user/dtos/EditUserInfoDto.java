package cn.graht.model.user.dtos;

import cn.graht.model.user.pojos.User;
import cn.graht.model.user.vos.UserVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @author GRAHT
 */

@Data
@Schema(description = "修改用户信息参数")
public class EditUserInfoDto {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String id;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String avatarUrl;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;


    /**
     * 标签列表json
     */
    @Schema(description = "标签列表json")
    private String tags;

    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    private String profile;

    /**
     * 封装类转对象
     *
     * @param userVo
     * @return
     */
    public static User voToObj(cn.graht.model.user.vos.UserVo userVo) {
        if (userVo == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(userVo, user);
        return user;
    }

    /**
     * 对象转封装类
     *
     * @param user
     * @return
     */
    public static cn.graht.model.user.vos.UserVo objToVo(User user) {
        if (user == null) {
            return null;
        }
        cn.graht.model.user.vos.UserVo userVo = new cn.graht.model.user.vos.UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }
}
