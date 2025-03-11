package cn.graht.model.socializing.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class CreateMessageDto {
    /**
     * 会话Id
     */
    private Integer sessionId;

    /**
     * 发送者Id
     */
    private String senderId;

    /**
     * 接收者Id
     */
    private String receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 是否已读 0 为否
     */
    private Integer isRead;
}
