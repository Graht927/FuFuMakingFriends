package cn.graht.model.organizeBureau.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
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
     * 队伍地址
     */
    private String address;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 创建人  队长
     */
    private String userId;

    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 搜索字段
     */
    private String searchText;
}