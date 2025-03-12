package cn.graht.socializing.controller.v1.notice;

import cn.graht.socializing.service.DynamicNoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

}
