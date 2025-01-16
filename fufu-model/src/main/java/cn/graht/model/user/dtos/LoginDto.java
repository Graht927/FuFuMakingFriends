package cn.graht.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;
import org.apache.logging.log4j.core.config.Scheduled;

/**
 * @author GRAHT
 */
@Data
@Schema(description = "登录参数")
public class LoginDto {
    /**
     * 手机号
     */
    @Schema(description = "手机号",example = "13777332211")
    private String phone;
    /**
     * 手机验证码
     */
    @Schema(description = "验证码")
    private String phoneCode;
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String userPassword;
    /**
     * 地址 不需要填
     */
    private String addr;
}

