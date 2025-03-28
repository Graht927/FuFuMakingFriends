package cn.graht.model.socializing.vos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class MessageVo {
    private Integer id;
    private String message;
    private String senderAvatar;
    private Boolean isSelf;
    private Date sendTime;
}
