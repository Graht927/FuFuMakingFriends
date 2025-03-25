package cn.graht.model.user.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * @author GRAHT
 */
@Data
@Schema(description = "创建用户动态参数")
public class CreateDynamicDto {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;
    /**
     * 动态内容
     */
    @Schema(description = "动态内容")
    private String content;
    /**
     * 图片
     */
    @Schema(description = "动态图片")
    private List<String> images;
    /**
     * 封面图片
     */
    @Schema(description = "封面图片")
    private String coverImages;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private String title;

}
