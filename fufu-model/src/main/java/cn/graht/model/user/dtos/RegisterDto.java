package cn.graht.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
@Schema(description = "注册参数")
public class RegisterDto {
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;
    /**
     * 手机号码
     */
    @Schema(description = "手机号码",example = "17333221132")
    private String phone;
    /**
     * 手机验证码
     */
    @Schema(description = "手机验证码")
    private String phoneCode;
    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    private String userPassword;
    /**
     * 用户密码二次校验
     */
    @Schema(description = "用户密码二次校验")
    private String checkPassword;
    /**
     * 地址 不需要填
     */
    private String addr;
}
