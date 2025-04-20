package cn.graht.model.search.vo;

import cn.graht.model.user.vos.UserDynamicVo;
import cn.graht.model.user.vos.UserVo;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author GRAHT
 */
@Data
public class FindVo {
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

    private  Integer size;
    private  String model;

    private Boolean isLike;

    private UserDynamicVo author;

    /**
     * 队伍名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;
    /**
     * 当前人数
     */
    private Integer currentNum;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 押金
     */
    private Double deposit;

    /**
     * 队伍头像
     */
    private String teamImage;
    /**
     * 队伍地址
     */
    private String address;
    /**
     * 过期时间
     */
    private Date expireTime;

    private UserVo leaderInfo;

    /**
     * 入队用户信息
     */
    private List<UserVo> teamUserInfos;
}
