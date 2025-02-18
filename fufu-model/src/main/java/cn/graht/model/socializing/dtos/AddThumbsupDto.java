package cn.graht.model.socializing.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class AddThumbsupDto {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 点赞动态
     */
    private Long dynamicId;

}
