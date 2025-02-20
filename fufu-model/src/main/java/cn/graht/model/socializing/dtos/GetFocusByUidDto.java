package cn.graht.model.socializing.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class GetFocusByUidDto extends PageQuery {
    //todo 未来看看是否需要对别人开放
    private String lookUid;
    private String uid;
    private String focusUid;
}
