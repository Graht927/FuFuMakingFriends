package cn.graht.socializing.service;


import cn.graht.common.commons.PageQuery;
import cn.graht.model.socializing.vos.NoticeVo;

import java.util.List;

/**
 * @author GRAHT
 */

public interface NoticeService  {
    List<NoticeVo> getAllNotice(String uid, PageQuery pageQuery);
    Integer getCountPage(String uid,Long pageSize);
}
