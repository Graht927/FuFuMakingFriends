package cn.graht.model.user.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class RegisterDto {
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String phone;
    /**
     * 手机验证码
     */
    private String phoneCode;
    /**
     * 用户密码
     */
    private String userPassword;
    /**
     * 用户密码二次校验
     */
    private String checkPassword;
    /**
     * 地址 不需要填
     */
    private String addr;
}
