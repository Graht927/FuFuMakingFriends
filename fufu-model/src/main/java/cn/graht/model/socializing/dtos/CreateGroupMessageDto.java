package cn.graht.model.socializing.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class CreateGroupMessageDto {
    /**
     * 群聊Id
     */
    private Integer groupId;

    /**
     * 发送者Id
     */
    private String senderId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String messageType;
}
