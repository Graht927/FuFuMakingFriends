package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.GetChildrenCommentsByCidAndPid;
import cn.graht.model.socializing.pojos.Comments;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.socializing.mapper.CommentsMapper;
import cn.graht.socializing.service.CommentsService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author GRAHT
* @description 针对表【comments(评论)】的数据库操作Service实现
* @createDate 2025-02-17 16:18:14
*/
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>
    implements CommentsService{

    @Resource
    private CommentsMapper commentsMapper;
    @Override
    //分页获取顶级评论
    public List<Comments> getParentCommentsByCid(Long cid, PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0, ErrorCode.PARAMS_ERROR);
        Page<Comments> commentsPage = new Page<>( pageQuery.getPageSize(),pageQuery.getPageNum());
        LambdaQueryWrapper<Comments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getDynamicId,cid);
        queryWrapper.eq(Comments::getParentCommentId,0);
        Page<Comments> page = commentsMapper.selectPage(commentsPage, queryWrapper);
        List<Comments> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) return List.of();
        else return records;
    }

    //通过cid分页获取子评论
    @Override
    public List<Comments> getCommentsByCid(Long cid, GetChildrenCommentsByCidAndPid getChildrenCommentsByCidAndPid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0, ErrorCode.PARAMS_ERROR);
        Page<Comments> commentsPage = new Page<>( getChildrenCommentsByCidAndPid.getPageSize(),getChildrenCommentsByCidAndPid.getPageNum());
        LambdaQueryWrapper<Comments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getDynamicId,cid);
        queryWrapper.eq(Comments::getParentCommentId,getChildrenCommentsByCidAndPid.getPid());
        Page<Comments> page = commentsMapper.selectPage(commentsPage, queryWrapper);
        List<Comments> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) return List.of();
        else return records;
    }


}




