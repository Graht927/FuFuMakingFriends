package cn.graht.model.user.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class GetDynamicByUidDto extends PageQuery {
    private String uid;
}
