package cn.graht.model.socializing.vos;

import lombok.Data;

import java.util.Date;

/**
 * @author GRAHT
 */
@Data
public class CommentsVo {
    private Long id;

    /**
     * 用户id
     */
    private String userId;
    private String nickname;
    private String avatarUrl;

    /**
     * 评论动态
     */
    private Long dynamicId;

    /**
     * 0 表示根评论
     */
    private Long parentCommentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建|发布时间
     */
    private Date createTime;

    private Integer childrenCount;


}
