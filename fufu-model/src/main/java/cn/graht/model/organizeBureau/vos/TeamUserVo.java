package cn.graht.model.organizeBureau.vos;

import cn.graht.model.user.vos.UserVo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍列表返回
 * @author GRAHT
 */
@Data
public class TeamUserVo implements Serializable {
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
     * 0 - 公开 1 - 加密
     */
    private Integer status;


    /**
     * 创建人  队长
     */
    private String userId;
    /**
     * 队伍头像
     */
    private String teamAvatarUrl;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 创建时间
     */
    private Date createTime;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 入队用户信息
     */
    private List<UserVo> teamUserInfos;
}