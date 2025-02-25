package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
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
     * 过期时间
     */
    private Date expireTime;
    /**
     * 如果加密|密码
     */
    private String password;

}
