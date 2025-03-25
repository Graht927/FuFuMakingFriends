package cn.graht.socializing.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.AddCommentsDto;
import cn.graht.model.socializing.dtos.GetChildrenCommentsByCidAndPid;
import cn.graht.model.socializing.pojos.Comments;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import cn.graht.socializing.service.CommentsService;

import java.util.List;

/**
 *  @author GRAHT
 */
@RestController
@RequestMapping("/v1/comments")
@Tag(name = "评论信息",description = "评论信息")
public class CommentsController {
    @Resource
    private CommentsService commentsService;
    @PostMapping("{dynamicId}")
    @Operation(summary = "通过dynamicId获取顶级评论信息|分页", description = "通过cid获取顶级评论信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<Comments>> getParentCommentsByCid(@PathVariable Long cid, @RequestBody PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0L, ErrorCode.PARAMS_ERROR);
        return ResultUtil.ok(commentsService.getParentCommentsByCid(cid,pageQuery));
    }
    @PostMapping("/c/{cid}")
    @Operation(summary = "通过cid获取子级评论信息|分页", description = "通过cid获取子级评论信息")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<Comments>> getCommentsByCid(@PathVariable Long cid, @RequestBody GetChildrenCommentsByCidAndPid getChildrenCommentsByCidAndPid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid) || cid < 0L, ErrorCode.PARAMS_ERROR);
        return ResultUtil.ok(commentsService.getCommentsByCid(cid,getChildrenCommentsByCidAndPid));
    }
    @PostMapping
    @Operation(summary = "添加评论", description = "添加评论")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<Comments> addComments(@RequestBody AddCommentsDto addCommentsDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(addCommentsDto), ErrorCode.PARAMS_ERROR);
        Comments comments = new Comments();
        BeanUtils.copyProperties(addCommentsDto, comments);
        boolean save = commentsService.save(comments);
        ThrowUtils.throwIf(!save, ErrorCode.NULL_ERROR);
        return ResultUtil.ok(comments);
    }
    @DeleteMapping("/{cid}")
    @Operation(summary = "删除评论", description = "删除评论")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<String> deleteComments(@PathVariable Long cid) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(cid)||cid < 0, ErrorCode.PARAMS_ERROR);
        boolean removeById = commentsService.removeById(cid);
        ThrowUtils.throwIf(!removeById, ErrorCode.NULL_ERROR);
        return ResultUtil.ok("删除成功!");
    }

}
