package cn.graht.model.socializing.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class CreateGroupChatMemberDto {
    /**
     * 群聊Id
     */
    private Integer groupId;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 角色
     */
    private Integer role;
}
