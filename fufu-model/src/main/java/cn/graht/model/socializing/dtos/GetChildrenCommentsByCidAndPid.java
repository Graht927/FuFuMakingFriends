package cn.graht.model.socializing.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class GetChildrenCommentsByCidAndPid extends PageQuery {
    private Long dynamicId;
    private Long pid;
}
