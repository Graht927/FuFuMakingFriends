package cn.graht.model.socializing.vos;

import lombok.Data;

import java.util.List;

/**
 * @author GRAHT
 */
@Data
public class CommentLastVo {
    private List<CommentsVo> commentsVoList;
    private Integer childrenCount;
}
