package cn.graht.model.socializing.dtos;

import lombok.Data;


/**
 * @author GRAHT
 */
@Data
public class CreateGroupSessionDto {
    /**
     * 群聊名称
     */
    private String name;

    /**
     * 创建者Id
     */
    private String creatorId;

    /**
     * 头像
     */
    private String avatarUrl;
    private String activityId;
    private String userId;
}
