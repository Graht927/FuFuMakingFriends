package cn.graht.socializing.controller.v1.notice;

import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import cn.graht.model.socializing.pojos.SystemNotice;
import cn.graht.socializing.service.DynamicNoticeService;
import cn.graht.socializing.service.SystemNoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GRAHT
 */
@RestController
@RequestMapping("/v1/system/notice")
@Tag(name = "系统通知中心",description = "系统通知中心")
public class SystemNoticeController {
    @Resource
    private SystemNoticeService systemNoticeService;

    @PostMapping("/add")
    public ResultApi<Boolean> addNotice(@RequestBody SystemNoticeDto noticeDto) {
        SystemNotice notice = new SystemNotice();
        BeanUtils.copyProperties(noticeDto, notice);
        return ResultUtil.ok(systemNoticeService.save(notice));
    }
}
