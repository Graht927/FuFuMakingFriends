package cn.graht.model.socializing.dtos;

import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class CreatePrivateSessionDto {
    /**
     * 用户1
     */
    private String userId1;

    /**
     * 用户2
     */
    private String userId2;
}
