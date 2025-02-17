package cn.graht.model.user.vos;

import lombok.Data;

import java.util.Date;

@Data
public class DynamicVo {
    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;


    /**
     * 动态内容
     */
    private String content;

    /**
     * 图片
     */
    private String images;

    /**
     * 封面图片
     */
    private String coverImages;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 转发数
     */
    private Integer forwardCount;

    /**
     * 创建|发布时间
     */
    private Date createTime;

}