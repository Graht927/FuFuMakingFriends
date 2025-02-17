package cn.graht.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author GRAHT
 */

@Data
@Schema(description = "修改用户动态参数")
public class EditDynamicDto {
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
     * 图片
     */
    private List<String> images;
    /**
     * 封面图片
     */
    private String coverImages;
}
