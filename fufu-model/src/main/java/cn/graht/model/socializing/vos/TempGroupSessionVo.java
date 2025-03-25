package cn.graht.model.socializing.vos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class TempGroupSessionVo {
    private Integer id;
    /**
     * 群聊名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;

    /**
     * 最新消息内容
     */
    private String lastMessageContent;

    private String senderId;


}
