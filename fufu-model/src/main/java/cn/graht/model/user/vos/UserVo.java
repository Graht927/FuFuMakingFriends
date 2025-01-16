package cn.graht.model.user.vos;

import cn.graht.model.user.pojos.User;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户Vo
 * @TableName user
 */
@Data
public class UserVo {
    /**
     * 用户id
     */
    private String id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 地区 例如[山西-晋城] [山西-太原]
     */
    private String addr;
    /**
     * 地区 例如[山西-晋城] [山西-太原]
     */
    private String upAddr;

    /**
     * 性别
     */
    private Integer gender;


    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0 -- 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 角色: 0--普通用户 1--VIP
     */
    private Integer userRole;

    /**
     * 标签列表json
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 封装类转对象
     *
     * @param userVo
     * @return
     */
    public static User voToObj(UserVo userVo) {
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
    public static UserVo objToVo(User user) {
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }
}