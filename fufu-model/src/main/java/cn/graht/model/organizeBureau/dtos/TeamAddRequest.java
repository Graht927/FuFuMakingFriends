package cn.graht.model.organizeBureau.dtos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
     * 过期时间
     */
    private Date expireTime;
    /**
     * 过期时间
     */
    private Date startTime;
    private String address;
    private List<String> teamImage;
    private Double deposit;
}
