package cn.graht.socializing.controller.v1.notice;

import cn.dev33.satoken.stp.StpUtil;
import cn.graht.common.commons.ErrorCode;
import cn.graht.common.commons.PageQuery;
import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.common.exception.ThrowUtils;
import cn.graht.model.socializing.vos.NoticeVo;
import cn.graht.socializing.service.DynamicNoticeService;
import cn.graht.socializing.service.NoticeService;
import cn.graht.socializing.service.SystemNoticeService;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/notice")
@Tag(name = "通知中心",description = "通知中心")
public class NoticeController {

    @Resource
    private NoticeService noticeService;
    @PostMapping("/{uid}}")
    @Operation(summary = "获取用户通知|分页", description = "获取用户通知|分页")
    @ApiResponse(responseCode = "200", description = "返回信息")
    @ApiResponse(responseCode = "40000", description = "参数错误")
    @ApiResponse(responseCode = "40002", description = "结果为空")
    public ResultApi<List<NoticeVo>> getNotice(@PathVariable String uid, @RequestBody PageQuery pageQuery) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(pageQuery), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(uid), ErrorCode.PARAMS_ERROR);
        String loginId = (String) StpUtil.getLoginId();
        ThrowUtils.throwIf(!loginId.equals(uid), ErrorCode.NO_AUTH);
        return ResultUtil.ok(noticeService.getAllNotice(uid, pageQuery));
    }
}
