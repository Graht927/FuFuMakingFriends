package cn.graht.model.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
@Schema(description = "短信请求参数")
public class SMSRequestParam {
    @Schema(description = "手机号",example = "13777332211")
    private String phone;
    @Schema(description = "短信模板")
    private String templateCodeStr;
    @Schema(description = "用户昵称")
    private String userNick;
    @Schema(description = "时间")
    private String time;
    @Schema(description = "地址")
    private String address;

}
