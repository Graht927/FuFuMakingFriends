package cn.graht.model.organizeBureau.pojos;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 * @TableName activity
 */
@TableName(value ="activity")
@Data
public class Activity implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 活动地址
     */
    private String address;

    /**
     * 创建人  队长
     */
    private String userId;

    /**
     * 押金
     */
    private Double deposit;

    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 活动图片
     */
    private String teamImage;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}