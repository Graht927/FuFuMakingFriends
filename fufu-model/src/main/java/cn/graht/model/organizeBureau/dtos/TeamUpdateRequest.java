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
     * 地址
     */
    private String address;
    /**
     * 当前人数
     */
    private Integer currentNum;
    /**
     * 最大人数
     */
    private Integer maxNum;
    /**
     * 押金
     */
    private Double deposit;

}
