package cn.graht.socializing.service.impl;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.feignApi.user.UserFeignApi;
import cn.graht.model.socializing.dtos.GetChildrenCommentsByCidAndPid;
import cn.graht.model.socializing.pojos.Comments;
import cn.graht.model.socializing.vos.CommentsVo;
import cn.graht.model.user.pojos.Dynamic;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.mapper.CommentsMapper;
import cn.graht.socializing.service.CommentsService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserFeignApi userFeignApi;

    @Override
    //分页获取顶级评论
    public List<CommentsVo> getParentCommentsByCid(Long cid, PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0, ErrorCode.PARAMS_ERROR);
        Page<Comments> commentsPage = new Page<>( pageQuery.getPageNum(),pageQuery.getPageSize());
        LambdaQueryWrapper<Comments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getDynamicId,cid);
        queryWrapper.eq(Comments::getParentCommentId,0);
        Page<Comments> page = commentsMapper.selectPage(commentsPage, queryWrapper);
        List<Comments> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) return List.of();
        else return records.stream().map(c->{
            CommentsVo commentsVo = new CommentsVo();
            BeanUtils.copyProperties(c,commentsVo);
            String userId = commentsVo.getUserId();
            ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(userId);
            if (ObjectUtils.isNotEmpty(userInfo)) {
                UserVo userVo = userInfo.getData();
                commentsVo.setAvatarUrl(userVo.getAvatarUrl());
                commentsVo.setNickname(userVo.getNickname());
            }
            LambdaQueryWrapper<Comments> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Comments::getParentCommentId,c.getId());
            queryWrapper1.eq(Comments::getDynamicId,cid);
            long count = commentsMapper.selectCount(queryWrapper1);
            commentsVo.setChildrenCount(Integer.parseInt(String.valueOf(count)));
            return commentsVo;
        }).toList();
    }

    //通过cid分页获取子评论
    @Override
    public List<CommentsVo> getCommentsByCid(Long cid, GetChildrenCommentsByCidAndPid getChildrenCommentsByCidAndPid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0, ErrorCode.PARAMS_ERROR);
        Page<Comments> commentsPage = new Page<>( getChildrenCommentsByCidAndPid.getPageNum(),getChildrenCommentsByCidAndPid.getPageSize());
        LambdaQueryWrapper<Comments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comments::getDynamicId,cid);
        queryWrapper.eq(Comments::getParentCommentId,getChildrenCommentsByCidAndPid.getPid());
        Page<Comments> page = commentsMapper.selectPage(commentsPage, queryWrapper);
        List<Comments> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) return List.of();
        else return records.stream().map(c->{
            CommentsVo commentsVo = new CommentsVo();
            BeanUtils.copyProperties(c,commentsVo);
            String userId = commentsVo.getUserId();
            ResultApi<UserVo> userInfo = userFeignApi.getUserInfo(userId);
            if (ObjectUtils.isNotEmpty(userInfo)) {
                UserVo userVo = userInfo.getData();
                commentsVo.setAvatarUrl(userVo.getAvatarUrl());
                commentsVo.setNickname(userVo.getNickname());
            }
            LambdaQueryWrapper<Comments> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(Comments::getParentCommentId,c.getId());
            queryWrapper1.eq(Comments::getDynamicId,cid);
            long count = commentsMapper.selectCount(queryWrapper1);
            commentsVo.setChildrenCount(Integer.parseInt(String.valueOf(count)));
            return commentsVo;
        }).toList();
    }


}




