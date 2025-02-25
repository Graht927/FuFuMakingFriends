package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamUpdateRequest implements Serializable {
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
     * 最大人数
     */
    private Integer maxNum;
    /**
     * 0 - 公开 1 - 私有 2 - 加密
     */
    private Integer status;
    /**
     * 如果加密|密码
     */
    private String password;

}
