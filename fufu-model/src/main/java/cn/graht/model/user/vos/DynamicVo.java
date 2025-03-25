package cn.graht.model.user.vos;

import lombok.Data;

import java.util.Date;
import java.util.List;

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
     * 标签
     */
    private String title;

    /**
     * 图片
     */
    private List<String> images;

    /**
     * 封面图片
     */
    private String image;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 创建|发布时间
     */
    private Date createTime;

    private final Integer size = 26;
    private final String model = "dynamic";

    private Boolean isLike;

    private UserDynamicVo author;

}