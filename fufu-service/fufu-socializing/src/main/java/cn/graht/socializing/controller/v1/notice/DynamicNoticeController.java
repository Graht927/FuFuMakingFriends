package cn.graht.socializing.controller.v1.notice;

import cn.graht.common.commons.ResultApi;
import cn.graht.common.commons.ResultUtil;
import cn.graht.model.socializing.dtos.DynamicNoticeDto;
import cn.graht.model.socializing.dtos.SystemNoticeDto;
import cn.graht.model.socializing.pojos.DynamicNotice;
import cn.graht.model.socializing.pojos.SystemNotice;
import cn.graht.socializing.service.DynamicNoticeService;
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
@RequestMapping("/v1/dynamic/notice")
@Tag(name = "动态通知中心",description = "动态通知中心")
public class DynamicNoticeController {
    @Resource
    private DynamicNoticeService dynamicNoticeService;

    @PostMapping("/add")
    public ResultApi<Boolean> addNotice(@RequestBody DynamicNoticeDto noticeDto) {
        DynamicNotice notice = new DynamicNotice();
        BeanUtils.copyProperties(noticeDto, notice);
        return ResultUtil.ok(dynamicNoticeService.save(notice));
    }
}
