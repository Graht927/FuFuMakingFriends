package cn.graht.model.socializing.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */

@Data
public class GetChildrenCommentsByCidAndPid extends PageQuery {
    private Long cid;
    private Long pid;
}
