package cn.graht.model.socializing.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class GetFansByUidDto extends PageQuery {
    private String uid;
    private String focusId;
}
