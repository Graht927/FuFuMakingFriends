package cn.graht.model.mq.dto.producer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @author GRAHT
 */

@Data
@Schema(description = "发送消息请求参数")
@Builder
public class SendMSGRequestParams {
    @Schema(description = "调用来源")
    private String from;
    @Schema(description = "消息主题")
    private String topic;
    @Schema(description = "消息内容")
    private MSGContentParams content;
    @Schema(description = "timeout")
    private Integer timeout;
    @Schema(description = "延迟等级")
    private Integer delayLevel;

    @Data
    @Schema(description = "消息内容对象")
    @Builder

    public static class MSGContentParams {

        @Schema(description = "用户id")
        private String uid;
        @Schema(description = "用户名")
        private String nickname;
        @Schema(description = "手机号")
        private String phone;
    }
}
