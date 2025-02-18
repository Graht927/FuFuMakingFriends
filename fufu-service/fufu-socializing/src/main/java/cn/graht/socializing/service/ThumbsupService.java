package cn.graht.socializing.service;

import cn.graht.common.commons.PageQuery;
import cn.graht.model.socializing.pojos.Thumbsup;
import cn.graht.model.socializing.vos.ThumbsupVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【thumbsUp(点赞)】的数据库操作Service
* @createDate 2025-02-17 16:18:14
*/
public interface ThumbsupService extends IService<Thumbsup> {

    List<ThumbsupVo> getThubmsUpByCid(Long cid, PageQuery pageQuery);
}
