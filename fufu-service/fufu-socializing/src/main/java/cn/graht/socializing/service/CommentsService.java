package cn.graht.socializing.service;

import cn.graht.common.commons.PageQuery;
import cn.graht.model.socializing.dtos.GetChildrenCommentsByCidAndPid;
import cn.graht.model.socializing.pojos.Comments;
import cn.graht.model.socializing.vos.CommentLastVo;
import cn.graht.model.socializing.vos.CommentsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author GRAHT
* @description 针对表【comments(评论)】的数据库操作Service
* @createDate 2025-02-17 16:18:14
*/
public interface CommentsService extends IService<Comments> {

    List<CommentsVo> getParentCommentsByCid(Long cid, PageQuery pageQuery);

    List<CommentsVo> getCommentsByCid(Long cid, GetChildrenCommentsByCidAndPid getChildrenCommentsByCidAndPid);

}
