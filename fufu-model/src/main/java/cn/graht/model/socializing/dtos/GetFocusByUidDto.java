package cn.graht.model.socializing.dtos;

import cn.graht.common.commons.PageQuery;
import lombok.Data;

/**
 * @author GRAHT
 */
@Data
public class GetFocusByUidDto {
    //todo 未来看看是否需要对别人开放
    private String userId;
    private String focusUid;
    /**
     * 每页展示条数
     */
    protected long pageSize;
    /**
     * 页码
     */
    protected long pageNum;
}
