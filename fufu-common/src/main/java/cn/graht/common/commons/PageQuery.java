package cn.graht.common.commons;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页对象
 * @author grhat
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageQuery {
    /**
     * 每页展示条数
     */
    protected long pageSize;
    /**
     * 页码
     */
    protected long pageNum;
}
