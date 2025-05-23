package cn.graht.socializing.controller.v1;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.dtos.EditFocusDto;
import cn.graht.model.socializing.dtos.GetFansByUidDto;
import cn.graht.model.socializing.dtos.GetFocusByUidDto;
import cn.graht.model.socializing.pojos.Focus;
import cn.graht.model.user.vos.UserVo;
import cn.graht.common.enums.NoticeType;
import cn.graht.socializing.event.FuFuEventEnum;
import cn.graht.socializing.event.FuFuEventPublisher;
import cn.graht.socializing.service.FocusService;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 *  @author GRAHT
 */
@RestController
@RequestMapping("/v1/focus")
@Tag(name = "关注信息",description = "关注信息")
public class FocusController {
    @Resource
    private FocusService focusService;
    @Resource
    private FuFuEventPublisher fuFuEventPublisher;

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
    @GetMapping("/focusNum")
    public ResultApi<Long> getFocusCount() {
        String loginId = (String) StpUtil.getLoginId();
        ThrowUtils.throwIf(StringUtils.isBlank(loginId), ErrorCode.PARAMS_ERROR);
        Long count = focusService.lambdaQuery().eq(Focus::getFocusId, loginId).count();
        return ResultUtil.ok(count);
    }
    @GetMapping("/fansNum")
    public ResultApi<Long> getFansCount() {
        String loginId = (String) StpUtil.getLoginId();
        ThrowUtils.throwIf(StringUtils.isBlank(loginId), ErrorCode.PARAMS_ERROR);
        Long count = focusService.lambdaQuery().eq(Focus::getUserId, loginId).count();
        return ResultUtil.ok(count);
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
        ThrowUtils.throwIf(!b,ErrorCode.SYSTEM_ERROR,"你已关注该用户");
        if (b) {
            //调用事件给被关注者发送通知
            String focusUserId = editFocusDto.getFocusUserId();
            fuFuEventPublisher.doStuffAndPublishAnEvent(FuFuEventEnum.SYSTEM_NOTICE.getValue(), Map.of("userId", focusUserId,
                    "type", NoticeType.FOCUS.getValue(), "focusUserId", editFocusDto.getUserId()));
            return ResultUtil.ok(true);
        }
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }
    @DeleteMapping
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
