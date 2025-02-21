package cn.graht.socializing.controller.v1;

import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.EditFocusDto;
import cn.graht.model.socializing.dtos.GetFansByUidDto;
import cn.graht.model.socializing.dtos.GetFocusByUidDto;
import cn.graht.model.user.vos.UserVo;
import cn.graht.socializing.service.FocusService;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  @author GRAHT
 */
@RestController
@RequestMapping("/v1/focus")
@Tag(name = "关注信息",description = "关注信息")
public class FocusController {
    @Resource
    private FocusService focusService;

    //获取的是我关注的列表
    @PostMapping("/getFocus")
    @Operation(summary = "获取关注列表|分页", description = "获取关注列表|分页")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<UserVo>> getFocusByUid(@RequestBody GetFocusByUidDto getFocusByUidDto) {
        //uid当前查看的人 focusUserId查看的人
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFocusByUidDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFocusByUidDto.getFocusUid())||ObjectUtils.isEmpty(getFocusByUidDto.getFocusUid()), ErrorCode.PARAMS_ERROR);
        return ResultUtil.ok(focusService.getFocusByUid(getFocusByUidDto));
    }
    //获取粉丝列表 获取别人关注我的列表

    @PostMapping("/getFans")
    @Operation(summary = "获取粉丝列表|分页", description = "获取粉丝列表|分页")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<UserVo>> getFansByUid(@RequestBody GetFansByUidDto getFansByUidDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFansByUidDto)||ObjectUtils.isEmpty(getFansByUidDto.getFocusId())||ObjectUtils.isEmpty(getFansByUidDto.getFocusId()), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(getFansByUidDto.getPageNum())||ObjectUtils.isEmpty(getFansByUidDto.getPageSize()), ErrorCode.PARAMS_ERROR);
        return ResultUtil.ok(focusService.getFansByUid(getFansByUidDto));
    }

    @PostMapping
    @Operation(summary = "关注", description = "关注")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> addFocus(@RequestBody EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getFocusUserId())||ObjectUtils.isEmpty(editFocusDto.getUserId()), ErrorCode.PARAMS_ERROR);
        Boolean b = focusService.addFocus(editFocusDto);
        if (b) {
            //todo 发送mq给user发送消息
            return ResultUtil.ok(true);
        }
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }
    @PostMapping("/del")
    @Operation(summary = "取消关注", description = "取消关注")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> delFocus(@RequestBody EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getFocusUserId())||ObjectUtils.isEmpty(editFocusDto.getUserId()), ErrorCode.PARAMS_ERROR);
        Boolean b = focusService.delFocus(editFocusDto);
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR);
        return ResultUtil.ok(true);
    }
    @PostMapping("/isFocusAndFans")
    @Operation(summary = "是否互相关注", description = "是否互相关注")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> isFocusAndFans(@RequestBody EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getFocusUserId())||ObjectUtils.isEmpty(editFocusDto.getUserId()), ErrorCode.PARAMS_ERROR);
        Boolean b = focusService.isFocusAndFans(editFocusDto);
        return ResultUtil.ok(b);
    }
    @PostMapping("/isFocus")
    @Operation(summary = "是否关注", description = "是否关注")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "50000", description = "系统内部错误")
    public ResultApi<Boolean> isFocus(@RequestBody EditFocusDto editFocusDto) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(editFocusDto.getFocusUserId())||ObjectUtils.isEmpty(editFocusDto.getUserId()), ErrorCode.PARAMS_ERROR);
        Boolean b = focusService.isFocus(editFocusDto);
        return ResultUtil.ok(b);
    }




}
