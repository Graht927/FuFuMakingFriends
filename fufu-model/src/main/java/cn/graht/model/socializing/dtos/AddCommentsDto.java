package cn.graht.model.socializing.dtos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class AddCommentsDto {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 评论动态
     */
    private Long dynamicId;

    /**
     * 0 表示根评论
     */
    private Long parentCommentId;

    /**
     * 评论内容
     */
    private String content;

}
