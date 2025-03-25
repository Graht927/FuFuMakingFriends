package cn.graht.model.socializing.vos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class TempPrivateSessionVo {
    private Integer id;

    private String uid;
    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;

    /**
     * 最新消息内容
     */
    private String lastMessageContent;

}
