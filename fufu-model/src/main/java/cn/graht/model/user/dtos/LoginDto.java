package cn.graht.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;
import org.apache.logging.log4j.core.config.Scheduled;

/**
 * @author GRAHT
 */
@Data
public class LoginDto {
    /**
     * 手机号
     */
    private String phone;
    /**
     * 手机验证码
     */
    private String phoneCode;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * 地址 不需要填
     */
    private String addr;
}

